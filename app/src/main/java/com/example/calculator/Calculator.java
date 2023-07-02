package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Calculator extends AppCompatActivity {

    /*
    * 计算器使用过程
    * 0. 初始状态什么都没有
    * 1. 输入第一个数字
    * 2. 选择运算符
    * 3. 输入第三个数字
    * 4. 点击 = 按钮 得出答案
    * */

    // 变量
    private int status;
    private EditText editText;
    private EditText resText;
    private double num1;//第一个数字
    private double num2;//第二个数字
    private String op;//运算符
    private StringBuilder expression;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        expression = new StringBuilder(); // 初始化表达式
        editText = findViewById(R.id.editText);
        editText.setText("0");
        resText = findViewById(R.id.resText);
        resText.setText("0");
    }

    public void doClick(View v){
        int id = v.getId();
        if (id == R.id.btn_UN) {
            fu();
        } else if (id == R.id.btn_AC) {
            reset();
        }else if (id == R.id.btn_DEL) {
            del();
        }
        else if(id == R.id.btn_GEN){
            gen();
        }else {
            f1(v);
        }
    }

    private void gen() {
        String str = editText.getText().toString();
        double d;
        try {
            d = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            reset();
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            return;
        }

        if (status == 3) {
            double result = end(num1, op, num2);
            resText.setText(String.valueOf(result)); // 将结果显示在 resText 文本框中
            d = result;
        }

        if (d < 0) {
            reset();
            Toast.makeText(this, "不能给负数开根号", Toast.LENGTH_SHORT).show();
        } else {
            double sqrtResult = Math.sqrt(d);
            resText.setText(String.valueOf(sqrtResult));  // 将根号结果显示在 resText 文本框中
            editText.setText(""); // 清空 editText 文本框
        }

        status = 0;
    }


    private void del() {
        String currentText = editText.getText().toString();
        int length = currentText.length();
        if (length > 0) {
            String newText = currentText.substring(0, length - 1);
            editText.setText(newText);

            switch (status) {
                case 0:
                    break;
                case 1:
                    num1 = newText.isEmpty() ? 0 : Double.parseDouble(newText);
                    break;
                case 2:
                    break;
                case 3:
                    num2 = newText.isEmpty() ? 0 : Double.parseDouble(newText);
                    break;
            }
        }
    }



    // 置空 归零
    private  void reset(){
        status = 0;
        num1 = 0;
        num2 = 0;
        op = null;
        editText.setText("0");
        resText.setText("0");
    }
    // 切换计算器
    private void fu() {
        String str = editText.getText().toString();
        try {
            double d = Double.parseDouble(str);
            editText.setText(String.valueOf(-d));
        }catch (NumberFormatException e){
            reset();
            Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show();
        }

        switch (status) {
            case 0:
                break;
            case 1:
                num1 = -num1;
                break;
            case 2:
                break;
            case 3:
                num2 = -num2;
                break;
        }
    }

    // 进行计算
    private void f1(View view){
        Button btn = (Button)view;
        String s = btn.getText().toString();
        // 根据点击按钮的类型不同进入不同的方法
        if(s.matches("\\d")){
            num(s);
        }else if(s.matches("[+\\-*/]")){
            operation(s);
        }else if(s.equals(".")){
            point();
        }else if(s.equals("=")){
            result();
        }
    }

    private void result() {
        switch(status){
            case 0:
                break;
            case 1:
                // 如果还没输入操作符，当作 +0 处理
                op = "+";
                num2 = 0;
                double num = end(num1, op, num2);
                num1 = num;
                status = 4;
                expression.setLength(0); // 清空表达式
                break;
            case 2:
                num = end(num1, op, 0);
                num1 = num;
                status = 4;
                expression.setLength(0); // 清空表达式
                break;
            case 3:
                double result = end(num1, op, num2);
                num1 = result;
                status = 4;
                expression.setLength(0); // 清空表达式
                break;
        }
        editText.setText(expression.toString());
    }


    private double end(double num1, String op, double num2) {
        double r = 0;
        switch (op.charAt(0)) {
            case '+':
                r = num1 + num2;
                break;
            case '-':
                r = num1 - num2;
                break;
            case '*':
                r = num1 * num2;
                break;
            case '/':
                if (num2 == 0) {
                    Toast.makeText(this, "除数不能为零", Toast.LENGTH_SHORT).show();
                } else {
                    r = num1 / num2;
                }
                break;
        }
        resText.setText(String.valueOf(r)); // 将结果显示在 resText 文本框中
        Toast.makeText(this, "计算完成", Toast.LENGTH_SHORT).show();
        return r;
    }

    private void point() {
        //不同状态下点“.”字符
        switch (status) {
            case 0:
                //初始状态下点“.”,会直接显示“0.”，此时的状态变成输入第一个数字
                editText.setText("0.");
                status = 1;
                break;
            case 1:
                //输入第一个数字时，再点“.”，需要判断是否已经含有“.”
                String s = editText.getText().toString();
                if (!s.contains(".")) {
                    editText.append(".");
                }
                break;
            case 2:
                num2 = 0;
                editText.setText("0.");
                status = 3;
                break;
            case 3:
                s = editText.getText().toString();
                if (!s.contains(".")) {
                    editText.append(".");
                }
                break;
            case 4:
                //点完等号再点“.”,相当于一次新的运算
                reset();
                editText.setText("0.");
                status = 1;
                break;
        }
    }

    private void operation(String s) {
        //不同状态下点击运算符
        switch (status) {
            //初始状态下点击运算符
            case 0:
                num1 = 0;
                status = 2;
                op = s;
                expression.append(num1).append(op); // 更新表达式
                editText.setText(expression.toString());
                break;
            //正在输入第一个数
            case 1:
                op = s;
                status = 2;
                expression.append(op); // 更新表达式
                editText.setText(expression.toString());
                break;
            //点完运算符，再点运算符
            case 2:
                op = s;
                break;
            //输入第二个数的时候点击运算符
            case 3:
                //求出上一次的结果作为新的计算的第一个数字
                num1 = end(num1, op, num2);
                editText.setText(String.valueOf(num1));
                op = s;
                expression.setLength(0); // 清空表达式
                expression.append(num1).append(op);
                status = 2;
                break;
            case 4:
                num1 = Double.parseDouble(editText.getText().toString());
                op = s;
                status = 2;
                break;
        }
    }

    private void num(String s) {
        //在不同状态下点击数字按钮
        switch (status) {
            //初始状态下，点击数字按钮直接显示
            case 0:
                if(s.equals("0")){
                    editText.setText(s);
                }else {
                    editText.setText(s);
                    num1 = Double.parseDouble(s);
                    expression.append(s); // 更新表达式
                }
                status = 1;
                break;
            case 1:
                if(editText.getText().toString().equals("0")){
                    editText.setText(s);
                    expression.append(s); // 更新表达式
                }else {
                    editText.append(s);
                }
                num1 = Double.parseDouble(editText.getText().toString());
                break;
            //点击符号后输入第二个数字
            case 2:
                editText.append(s);
                num2 = Double.parseDouble(s);
                status = 3;
                expression.append(s); // 更新表达式
                editText.setText(expression.toString());
                break;
            //输入第二个数字的状态
            case 3:
                if(editText.getText().toString().equals("0")){
                    editText.setText(s);
                }else {
                    editText.append(s);
                    expression.append(s); // 更新表达式
                }
                num2 = Double.parseDouble(editText.getText().toString());
                editText.setText(expression.toString());
                break;
            case 4: //新的状态，表示已经完成一次计算，但是用户继续输入数字，因此应该开始新的计算
                editText.setText(s);
                num1 = Double.parseDouble(s);
                op = null;
                status = 1;
                expression.setLength(0); // 清空表达式
                expression.append(s); // 更新表达式
                break;
        }
    }

}