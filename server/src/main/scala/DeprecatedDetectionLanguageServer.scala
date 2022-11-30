package com.github.chencmd.impdocutil

import scala.util.chaining.*

import generic.IOExtra
import generic.extensions.DocumentUriExt.*

import jsonrpclib.fs2.catsMonadic

import langoustine.lsp.{
  requests as R,
  structures as S,
  enumerations as E,
  aliases as A,
  *
}
import langoustine.lsp.app.LangoustineApp
import langoustine.lsp.runtime.*

import cats.effect.IO

import io.scalajs.nodejs.path.Path
import io.scalajs.nodejs.url.URL
import io.scalajs.nodejs.fs.*

object DeprecatedDetectionLanguageServer extends LangoustineApp.Simple {

  def server: IO[LSPBuilder[IO]] = IO(create)

  def create: LSPBuilder[IO] = {
    LSPBuilder
      .create[IO]
      .handleRequest(R.initialize) { in =>
        sendMessage(
          in.toClient,
          E.MessageType.Info,
          "server activated"
        ) as S.InitializeResult(
          S.ServerCapabilities(
            diagnosticProvider = Opt(
              S.DiagnosticOptions(
                interFileDependencies = true,
                workspaceDiagnostics = false
              ))
          ),
          Opt(
            S.InitializeResult.ServerInfo(
              name = "Deprecated Detection",
              version = Opt("0.0.1")
            )
          )
        )
      }
      .handleRequest(R.textDocument.diagnostic) { in =>
        for {
          docUri <- IO.pure(in.params.textDocument.uri)
          target <- getDocument(docUri)
          targetDoc <- for {
            targetPath <- IO.pure(docUri.parent / target)
            existsFile <- exists(targetPath)
            doc <- IOExtra.whenA(existsFile)(getDocument(targetPath))
          } yield doc

          report <- IO.pure {
            S.RelatedFullDocumentDiagnosticReport(
              relatedDocuments = Opt.empty,
              kind = "full",
              resultId = Opt.empty,
              items = if targetDoc.exists(s => s.contains("deprecated")) then {
                Vector(
                  S.Diagnostic(
                    range =
                      S.Range(S.Position(0, 0), S.Position(0, target.length())),
                    message = s"deprecated file: $target",
                    tags = Opt(Vector(E.DiagnosticTag.Deprecated))
                  )
                )
              } else {
                Vector.empty
              }
            )
          }
        } yield A.DocumentDiagnosticReport(report)
      }
  }

  def sendMessage(
      back: Communicate[IO],
      messageType: E.MessageType,
      msg: String
  ): IO[Unit] = {
    back.notification(
      R.window.showMessage,
      S.ShowMessageParams(messageType, msg)
    )
  }
}
