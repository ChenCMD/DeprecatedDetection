import path from 'path'
import * as vsc from 'vscode'
import * as lc from 'vscode-languageclient/node.js'

let client: lc.LanguageClient | undefined

export function activate(context: vsc.ExtensionContext) {
  // The server is implemented in node
  const serverModule = vsc.Uri.joinPath(context.extensionUri, 'server', 'target', 'scala-3.2.1', 'impdocutil-fastopt', 'main.js').fsPath;
  // The debug options for the server
  // --inspect=6037: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
  const debugOptions = {
    execArgv: ['--nolazy', '--inspect=6037', '--expose-gc']
  }

  // If the extension is launched in debug mode then the debug server options are used
  // Otherwise the run options are used
  const serverOptions: lc.ServerOptions = {
    run: {
      module: serverModule,
      transport: lc.TransportKind.ipc,
    },
    debug: {
      module: serverModule,
      transport: lc.TransportKind.ipc,
      options: debugOptions,
    }
  }

  const documentSelector: lc.DocumentSelector = [
    { language: 'mcfunction' }
  ]

  // Options to control the language client
  const clientOptions: lc.LanguageClientOptions = {
    documentSelector,
    initializationOptions: {},
    progressOnInitialization: true
  }

  // Create the language client and start the client.
  client = new lc.LanguageClient(
    'IMPDocUtility',
    'IMPDoc Utility',
    serverOptions,
    clientOptions
  )

  // Start the client. This will also launch the server
  client.start()
}

export function deactivate(): Thenable<void> | undefined {
  return client?.stop()
}