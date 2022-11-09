package com.github.chencmd.impdocutil

import scala.util.chaining.*

import jsonrpclib.fs2.catsMonadic

import langoustine.lsp.*
import langoustine.lsp.app.LangoustineApp
import langoustine.lsp.runtime.*

import cats.effect.kernel.Async
import cats.effect.{IO, Resource}
import cats.implicits.given

import fs2.io.file.Path
import fs2.io.file.Files
import fs2.text

object IMPDocUtilityLanguageServer extends LangoustineApp {
  import requests as R
  import structures as S
  import enumerations as E

  def server(args: List[String]): Resource[IO, LSPBuilder[IO]] = {
    Resource.make(IO(create))(_ =>
      IO.consoleForIO.errorln("Terminating server")
    )
  }

  def create: LSPBuilder[IO] = {
    LSPBuilder
      .create[IO]
      .handleRequest(R.initialize) { inv =>
        for {
          _ <- inv.toClient.notification(
            R.window.showMessage,
            S.ShowMessageParams(E.MessageType.Info, "test")
          )
          res <- IO {
            S.InitializeResult(
              S.ServerCapabilities(
                documentFormattingProvider = Opt(true)
              ),
              Opt(
                S.InitializeResult.ServerInfo(
                  name = "neko",
                  version = Opt("0.0.1")
                )
              )
            )
          }
        } yield res
      }
    // .handleNotification(R.textDocument.formatting) { (inv: Invocation[R.textDocument.formatting.In, IO]) =>
    //   inv.params.textDocument.uri.getDocument[IO]
    // }
  }

  extension (uri: DocumentUri) {
    def getDocument[F[_]: Async](): F[String] = {
      uri.value
        .drop("file://".length)
        .pipe(Path.apply)
        .pipe(Files[F].readAll)
        .through(text.utf8.decode)
        .compile
        .string
    }
  }
}
