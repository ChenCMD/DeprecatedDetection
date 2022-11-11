import * as vsc from 'vscode';
import * as lc from 'vscode-languageclient/node.js';

let client: lc.LanguageClient | undefined;

export function activate({ extensionUri }: vsc.ExtensionContext): void {
  vsc.window.showInformationMessage("client activated!!!!");

  // The server is implemented in node
  const serverModule = vsc.Uri.joinPath(extensionUri, 'server', 'dist', 'main.js').fsPath;
  // The debug options for the server
  // --inspect=6037: runs the server in Node's Inspector mode so VS Code can attach to the server for debugging
  const debugOptions = {
    execArgv: ['--nolazy', '--inspect=6037', '--expose-gc']
  };

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
  };

  const documentSelector: lc.DocumentSelector = [
    { language: 'mcfunction' }
  ];

  // Options to control the language client
  const clientOptions: lc.LanguageClientOptions = {
    documentSelector,
    initializationOptions: {},
    progressOnInitialization: true
  };

  // Create the language client and start the client.
  try {
    client = new lc.LanguageClient(
      'IMPDocUtility',
      'IMPDoc Utility',
      serverOptions,
      clientOptions
    );
  } catch (e) {
    console.error("e1:", e);
  }

  // Start the client. This will also launch the server
  client?.start()
    .then(() => console.log('server activated!!'))
    .catch(e => console.error("e2:", e));
}

export async function deactivate(): Promise<void> {
  return client?.needsStop() ? await client.stop() : undefined;
}

async function setTimeOut(millisecond: number): Promise<never> {
  // eslint-disable-next-line brace-style
  return await new Promise((_, reject) => setTimeout(
    () => reject("timeout"),
    millisecond
  ));
}