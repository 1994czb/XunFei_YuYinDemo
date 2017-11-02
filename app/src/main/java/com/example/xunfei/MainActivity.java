package com.example.xunfei;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.xunfei.bean.XunFeiBean;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private StringBuilder stringBuilder;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.editText);
    }


    /*
    * 语音转文字的功能
    *
    * */
    public void listen(View view) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");  //设置语言  中文
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin"); // 设置语言类型  普通话
        //若要将UI控件用于语义理解，必须添加以下参数设置，设置之后onResult回调返回将是语义理解
        //结果
        //	mDialog.setParameter("asr_sch", "1");
        //	mDialog.setParameter("nlp_version", "2.0");

        //因为 文字和标点符号是分开的  我们要把他们连接起来  还需要创建一个容器
        stringBuilder = new StringBuilder();
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {

            //recognizerResult  识别的结果，Json格式的字符串
            //第二参数   b   判断语音开始和结束  为false时  一直处于运行的状态，会一直回调该方法  为true时 停止回话  停止回调
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean b) {
                //拿到语音识别后返回String串的结果
                String resultString = recognizerResult.getResultString();
                Log.e("TAG", "讯飞语音识别的结果: " + resultString);
                Log.e("TAG", "第二个参数b的值: " + b);

                //解析后的数据
                String content = parseData(resultString);
                Log.e("TAG", "解析后的数据:" + content + "");
                //将解析后的数据添加到容器
                stringBuilder.append(content);
                if (b) {
                    String string = stringBuilder.toString();
                    Log.e("TAG", "解析的最终结果:" + string);
                    editText.setText(string);


                    /**
                     * 语音对答
                     */
                    //创建默认的回答语音
                    String answer = "温馨提示，请提高您的说话音量！";
                    if (string.contains("你好")) {
                        answer = "您好！我是科大讯飞语音小助手，很高兴为您服务！";
                    } else if (string.contains("你期望的薪资是多少")) {
                        answer = "我的目标薪资是三万八，达不到目标我就再喊一遍";
                    } else if (string.contains("你是哪儿的人")) {
                        String[] stringList = new String[]{"山西人", "上海人", "北京人", "东北人", "广东人"};
                        int position = (int) (Math.random() * stringList.length);
                        answer = stringList[position];
                    }
                    speak(answer);
                }
            }

            @Override
            public void onError(SpeechError speechError) {
                Log.e("TAG", "错误码是:" + speechError + "");
            }
        });


        // /4.显示dialog，接收语音输入
        mDialog.show();


    }

    //解析json的方法
    private String parseData(String stringResult) {
        Gson gson = new Gson();
        XunFeiBean xunFeiBean = gson.fromJson(stringResult, XunFeiBean.class);
        ArrayList<XunFeiBean.WS> result = xunFeiBean.ws;
        //看数据  因为识别出来的文字是不连贯  我们需要的是把他弄成连续的一句话  所以我门要创建一个容器来存放拿到的数据
        //StringBuilder 比 StringBuffer的效率高
        StringBuilder stringBuilder = new StringBuilder();
        for (XunFeiBean.WS data : result) {
            String text = data.cw.get(0).w;
            stringBuilder.append(text);
        }

        return stringBuilder.toString();
    }

    /*
    * 文字转语音的功能
    *
    * */
    public void talk(View view) {

        speak("科大讯飞，让世界聆听我们的声音");

    }

    private void speak(String result) {

        //1.创建 SpeechSynthesizer 对象, 第二个参数：本地合成时传InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(this, null);
        //2.合成参数设置，详见《MSC Reference Manual》SpeechSynthesizer 类
        // 设置发音人（更多在线发音人，用户可参见 附录13.2
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");// 设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围 0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端


        //设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
        //保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
        //仅支持保存为 pcm 和 wav 格式，如果不需要保存合成音频，注释该行代码
        //mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");


        //3.开始合成
        mTts.startSpeaking(result, null);

    }


}
