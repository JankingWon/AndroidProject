package com.example.jason.finalproj.Login;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jason.finalproj.DB;
import com.example.jason.finalproj.R;

import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginFragment extends Fragment {
    //private MyVideoView myVideoView;
    //private com.github.clans.fab.FloatingActionMenu floatmenu;
    private com.dd.CircularProgressButton loginbtn;
    private Button tosignbtn;
    private EditText pwdInput;
    private CheckBox rmaccount;
    private CheckBox rmpwd;
    private AutoCompleteTextView accountInput;
    private ArrayAdapter arrayAdapter;
    private com.example.jason.finalproj.DB mydb;
    private String[] historyAccount = new String[]{};
    private String historyPwd;
    private final static String SHAREDNAME = "account_data";
    private final static int DHACCOUNT = 0;
    private final static int LOADSUCCESSED = 1;
    private final static int LOADFAILED = 2;
    private final static int RUNTIMEERRO = 4;
    private Handler handler;
    private int u_id = -1;
    private String nowaccount;
    private String nowpwd;
    private boolean rmAccount;
    private boolean rmPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmet_login,container,false);
        //floatmenu = (com.github.clans.fab.FloatingActionMenu) view.findViewById(R.id.loginmenu);
        //loginbtn = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.loginbtn);
        // myVideoView = (MyVideoView) view.findViewById(R.id.myvideo);
        loginbtn = (com.dd.CircularProgressButton) view.findViewById(R.id.loginbtn);
        tosignbtn = (Button) view.findViewById(R.id.tosignbtn);
        pwdInput = (EditText) view.findViewById(R.id.editpwd);
        rmaccount =(CheckBox) view.findViewById(R.id.rmaccount);
        rmpwd = (CheckBox) view.findViewById(R.id.rmpwd);
        accountInput = (AutoCompleteTextView) view.findViewById(R.id.editaccount);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        setHistoryAccount();
        setLogin_btn();
        mydb = new DB();
        setHandler();
        tosignbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //floatmenu.close(true);
                LoginActivity loginActivity = (LoginActivity) getActivity();
                loginActivity.showSign();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    public void init(){
      /*  String video_path = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.loadbg;
        myVideoView.setVideoURI(Uri.parse(video_path));
        myVideoView.start();
        myVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                myVideoView.start();
            }
        });*/
        rmAccount = false;
        rmPassword = false;
        clear();
    }

    public void setLogin_btn(){
        accountInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickAccount = (TextView) view;
                setHistoryPwd(clickAccount.getText().toString());
                rmaccount.setChecked(true);
                if(!historyPwd.isEmpty()){
                    pwdInput.setText(historyPwd);
                    rmpwd.setChecked(true);
                }
            }
        });
        loginbtn.setIndeterminateProgressMode(true);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginbtn.getProgress()!=0)loginbtn.setProgress(0);
                else{
                    historyPwd = "";
                    nowaccount = accountInput.getText().toString();
                    nowpwd = pwdInput.getText().toString();
                    if(nowaccount.isEmpty()||nowpwd.isEmpty()) Toast.makeText(getActivity(),"账号或密码为空",Toast.LENGTH_SHORT).show();
                    else{
                        if(rmaccount.isChecked()) rmAccount = true;
                        else rmAccount = false;
                        if(rmpwd.isChecked()) rmPassword = true;
                        else rmPassword = false;
                        loginbtn.setProgress(50);
                        submit(nowaccount,nowpwd);
                    }
                }
            }
        });
    }

    public void setHistoryAccount(){
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHAREDNAME,getActivity().MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("account",null);
        if(set!=null&&!set.isEmpty()){
            historyAccount = set.toArray(new String[set.size()]);
        }
        arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,historyAccount);
        accountInput.setAdapter(arrayAdapter);
    }

    public void setHistoryPwd(String account){
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHAREDNAME,getActivity().MODE_PRIVATE);
        historyPwd = sharedPreferences.getString(account,"");
    }

    public boolean HistoryhasAccount(String account){
        List<String> tempList = Arrays.asList(historyAccount);
        // 利用list的包含方法,进行判断
        if(tempList.contains(account)) return true;
        else return false;
    }

    public void submit(final String account,final String pwd){
        final  Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    Connection con = mydb.getConnection();
                    if (mydb.isaccount(account, con)) {
                        if (mydb.confirmPassword(account, pwd, con)) {
                            Message mesege = new Message();
                            mesege.what = LOADSUCCESSED;
                            mesege.arg1 = mydb.getUidByAccount(account, con);
                            handler.sendMessage(mesege);
                            mydb.closeConnection(con);
                        } else handler.obtainMessage(LOADFAILED).sendToTarget();
                    } else handler.obtainMessage(DHACCOUNT).sendToTarget();
                }catch (RuntimeException e){
                    handler.obtainMessage(RUNTIMEERRO).sendToTarget();
                }
            }
        });
        thread.start();
    }

    public void setHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case DHACCOUNT:
                        loginbtn.setProgress(-1);
                        Toast.makeText(getActivity(),"账号不存在",Toast.LENGTH_SHORT).show();
                        break;
                    case LOADFAILED:
                        loginbtn.setProgress(-1);
                        Toast.makeText(getActivity(),"账号或密码错误",Toast.LENGTH_SHORT).show();
                        break;
                    case LOADSUCCESSED:
                        loginbtn.setProgress(100);
                        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHAREDNAME,getActivity().MODE_PRIVATE);
                        if(rmAccount){
                            if(!HistoryhasAccount(nowaccount)){
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                Set<String> set = new HashSet<>(Arrays.asList(historyAccount));
                                set.add(nowaccount);
                                editor.putStringSet("account",set);
                                editor.commit();
                                setHistoryAccount();
                                //Log.i("test","保存Account成功");
                            }
                        }
                        if(rmPassword){
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(nowaccount,nowpwd);
                            editor.commit();
                        }
                        u_id = msg.arg1;
                        new android.os.Handler().postDelayed(new Runnable() {
                            public void run() {
                                ((LoginActivity) getActivity()).loadsuccessed(u_id);
                            }

                        }, 1000);
                        break;
                    case RUNTIMEERRO:
                        loginbtn.setProgress(-1);
                        Toast.makeText(getActivity(),"请检查网络连接",Toast.LENGTH_SHORT).show();
                        break;
                    default:break;
                }
            }
        };
    }

    public void clear(){
        pwdInput.setText("");
        rmaccount.setChecked(false);
        rmpwd.setChecked(false);
        accountInput.setText("");
    }
}
