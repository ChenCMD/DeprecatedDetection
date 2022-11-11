package com.github.chencmd.impdocutil

import scala.util.chaining.*

import jsonrpclib.fs2.catsMonadic

import langoustine.lsp.{requests as R, structures as S, enumerations as E, *}
import langoustine.lsp.app.LangoustineApp
import langoustine.lsp.runtime.*

import cats.effect.IO
import cats.implicits.given

import fs2.io.file.{Path, Files}
import fs2.text.*

object IMPDocUtilityLanguageServer extends LangoustineApp.Simple {

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
            // documentFormattingProvider = Opt(true)
          ),
          Opt(
            S.InitializeResult.ServerInfo(
              name = "IMPDoc Utility",
              version = Opt("0.0.1")
            )
          )
        )
      }
  }

  def sendMessage(
      back: Communicate[IO],
      messageType: E.MessageType,
      msg: String
  ): IO[Unit] = {
    back.notification(
      R.window.showMessage,
      S.ShowMessageParams(messageType, msg))
  }

  extension (uri: DocumentUri) {
    def getDocument(): IO[String] = {
      uri.value
        .drop("file://".length)
        .pipe(Path.apply)
        .pipe(Files[IO].readAll)
        .through(utf8.decode)
        .compile
        .string
    }
  }
}
