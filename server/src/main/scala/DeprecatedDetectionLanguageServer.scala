import generic.*
import generic.extensions.DocumentUriExt.*
import generic.extensions.TextDocumentExt.*

import jsonrpclib.fs2.catsMonadic

import langoustine.lsp.{
  requests as R,
  structures as S,
  enumerations as E,
  aliases as A,
  *
}
import langoustine.lsp.structures.{TextDocumentIdentifier as TextDocument}
import langoustine.lsp.app.LangoustineApp
import langoustine.lsp.runtime.*

import cats.effect.IO

object DeprecatedDetectionLanguageServer extends LangoustineApp.Simple {

  def server: IO[LSPBuilder[IO]] = IO(create)

  def create: LSPBuilder[IO] = {
    LSPBuilder
      .create[IO]
      .handleRequest(R.initialize) { in =>
        IO {
          S.InitializeResult(
            S.ServerCapabilities(
              diagnosticProvider = Opt(
                S.DiagnosticOptions(
                  interFileDependencies = true,
                  workspaceDiagnostics = false
                )
              )
            ),
            Opt(
              S.InitializeResult.ServerInfo(
                name = "Deprecated Detection",
                version = Opt("0.1.0")
              )
            )
          )
        }
      }
      .handleRequest(R.textDocument.diagnostic) { in =>
        for {
          cDoc <- IO.pure(in.params.textDocument)
          cDocContents <- cDoc.getText()

          tDoc <- IO.pure(TextDocument(cDoc.uri.parent / cDocContents))
          existsTargetDoc <- tDoc.exists()
          tDocContents <- IOExtra.whenA(existsTargetDoc)(tDoc.getText())

          isDocDeprecated <- IO.pure(tDocContents.exists(_ == "deprecated"))

        } yield A.DocumentDiagnosticReport(
          S.RelatedFullDocumentDiagnosticReport(
            relatedDocuments = Opt.empty,
            kind = "full",
            resultId = Opt.empty,
            items = MonoidExtra.whenMonoid(isDocDeprecated) {
              Vector(
                S.Diagnostic(
                  range = S.Range(
                    S.Position(0, 0),
                    S.Position(0, cDocContents.length())),
                  message = s"deprecated file: $cDocContents",
                  tags = Opt(Vector(E.DiagnosticTag.Deprecated))
                )
              )
            }
          )
        )
      }
  }
}
