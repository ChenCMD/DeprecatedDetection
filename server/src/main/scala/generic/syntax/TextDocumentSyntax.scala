package generic.syntax

import scala.util.chaining.*

import DocumentUriSyntax.*

import langoustine.lsp.structures.{TextDocumentIdentifier as TextDocument}

import cats.effect.IO

import io.scalajs.nodejs.fs.{Fs, FsExtensions}

object TextDocumentSyntax {
  extension (textDocument: TextDocument) {
    def getText(): IO[String] = {
      IO(Fs.readFileFuture(textDocument.uri.toPath, "utf8")).pipe(IO.fromFuture)
    }

    def exists(): IO[Boolean] = {
      IO(Fs.existsFuture(textDocument.uri.toPath)).pipe(IO.fromFuture)
    }
  }
}
