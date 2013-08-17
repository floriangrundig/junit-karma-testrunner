package de.is24.util.karmatestrunner.jetty;

import de.is24.util.karmatestrunner.TestReporter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;

public class ResultReceiverServer extends Server {
    WebSocket _websocket;
    SelectChannelConnector _connector;
    WebSocketHandler _wsHandler;

    TestReporter reporter;

    public ResultReceiverServer(int port) {
        _connector = new SelectChannelConnector();
        _connector.setPort(port);

        addConnector(_connector);
        _wsHandler = new WebSocketHandler() {
            public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
                if ("karma-test-results".equals(protocol)) {
                    _websocket = new MyTestWebSocket();
                }
                return _websocket;
            }
        };

        setHandler(_wsHandler);
    }

    public void setTestReporter(TestReporter testReporter){
        this.reporter = testReporter;
    }


  /* ------------------------------------------------------------ */

    class MyTestWebSocket implements WebSocket, WebSocket.OnFrame, WebSocket.OnTextMessage, WebSocket.OnControl {
        protected FrameConnection _connection;

        public FrameConnection getConnection() {
            return _connection;
        }

        public void onOpen(Connection connection) {
        }

        public void onHandshake(FrameConnection connection) {
            _connection = connection;
        }

        public void onClose(int code, String message) {
        }

        public boolean onFrame(byte flags, byte opcode, byte[] data, int offset, int length) {
            return false;
        }

        public boolean onControl(byte controlCode, byte[] data, int offset, int length) {
            return false;
        }

        public void onMessage(String data) {
            reporter.handleMessage(data);
        }

    }
}