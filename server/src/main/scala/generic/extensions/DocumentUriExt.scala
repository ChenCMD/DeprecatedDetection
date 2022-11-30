package generic.extensions

import scala.util.chaining.*

import langoustine.lsp.runtime.DocumentUri

import cats.effect.IO

import io.scalajs.nodejs.path.Path
import io.scalajs.nodejs.url.URL
import io.scalajs.nodejs.fs.*


object DocumentUriExt {
  extension (docUri: DocumentUri) {
    def parent: DocumentUri = {
      Path.dirname(docUri.toPath).pipe(pathToUri)
    }

    def /(after: String): DocumentUri = {
      s"${docUri.toPath}/$after".pipe(pathToUri)
    }

    def toPath: String = {
      URL.fileURLToPath(docUri.value)
    }
  }

  private def pathToUri(path: String): DocumentUri = {
    DocumentUri(URL.pathToFileURL(path).toString)
  }
}
