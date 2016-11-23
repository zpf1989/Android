package mynet.signalr.android.chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.internal.ObjectConstructor;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class ChatActivity extends AppCompatActivity {
    private final static String KEY="signalrChat";
    private final static String url_hub="http://192.168.18.89/signalr_chat/";
    private HubConnection _connection;
    private HubProxy _hub;

    private EditText txtSend;
    private TextView lblState;

    private ListView listHistory;
    MyAdapter listItemAdapter;
    ArrayList<String> listItems;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        txtSend=(EditText)findViewById(R.id.txtSend);
        lblState=(TextView)findViewById(R.id.lblState);

        listHistory=(ListView)findViewById(R.id.listHistory);
        listItems=new ArrayList<String>();
        listItemAdapter=new MyAdapter(this,listItems);
        listHistory.setAdapter(listItemAdapter);

        //创建属于主线程的handler
        handler=new Handler();
        bindEvents();
    }

    private  void bindEvents(){
        //注册按钮事件
        Button btnSend=(Button)findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                String msg=txtSend.getText().toString();
                if(msg.isEmpty()){
                    return;
                }
                String json="{\"H\":\"chathub\",\"M\":\"Send\",\"A\":[\"android_client\",\""+msg+"\"],\"I\":8}";
                printMsg("btnSend clicked,going to send :"+json);
                _connection.send(json);
                printMsg("btnSend clicked, '"+msg+"' sent");
            }
        });

        Button btnConnect=(Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener(){
            public  void onClick(View v){
                //启动连接
                beginConnect();;
            }
        });

        Button btnDisConnect=(Button)findViewById(R.id.btnDisConnect);
        btnDisConnect.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                _connection.disconnect();
            }
        });
    }

    private void beginConnect(){
        _connection=new HubConnection(url_hub);
        try{
            _hub=_connection.createHubProxy("ChatHub");

            _hub.subscribe(new Object(){
                @SuppressWarnings("unused")
                public  void broadcastMessage(String name,String msg){
//                    printMsg(name + ": " + msg);
                    handler.post(new NotifyRunnable(name,msg));
                }
            });

            _connection.error(new ErrorCallback() {
                @Override
                public void onError(Throwable error) {
//                    error.printStackTrace();
                    printMsg("error,"+error.getMessage());
                }
            });
            _connection.connected(new Runnable() {
                @Override
                public void run() {
                    printMsg("CONNECTED");
                    handler.post(new StateRunnable("已连接"));
                }
            });
            _connection.closed(new Runnable() {
                @Override
                public void run() {
                    printMsg("DISCONNECTED");
                    handler.post(new StateRunnable("已断开"));
                }
            });

            //start
            _connection.start()
                    .done(new Action<Void>() {
                        @Override
                        public void run(Void obj) throws Exception {
                            printMsg("Done Connecting!");
                        }
                    });
            //Subscribe to the received event
            _connection.received(new MessageReceivedHandler() {
                @Override
                public void onMessageReceived(JsonElement json) {
                    printMsg("RAW received message: " + json.toString());
                }
            });
        }catch (Exception ex){
            printMsg("createHubProxy error:"+ex.getMessage());
        }
    }

    private  void printMsg(String msg){
        System.out.println(KEY+":"+msg);
    }

    class StateRunnable implements Runnable{
        String _state;
        public StateRunnable(String state){
            _state=state;
        }
        public void run() {
            lblState.setText(_state);
        }
    }

    class NotifyRunnable implements Runnable{
        String _name;
        String _msg;
        public NotifyRunnable(String name,String msg){
            _name=name;
            _msg=msg;
        }
        public void run() {
            String data=_name+":"+_msg;
//            printMsg(data);
            listItems.add(data);
            listItemAdapter.notifyDataSetChanged();
        }
    }
}
