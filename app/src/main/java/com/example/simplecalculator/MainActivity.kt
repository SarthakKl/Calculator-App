package com.example.calculator


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.simplecalculator.R

class Stack<T>(){
    private val stk:ArrayList<T> = arrayListOf()

    fun push(x:T){
        stk.add(x)
    }
    fun pop():T{
        val size=stk.size
        val removed=stk[size-1]
        stk.removeAt(size-1)

        return removed
    }
    fun Top():T{
        return stk[stk.size - 1]
    }
    fun isEmpty():Boolean{
        return stk.size==0
    }
}
class MainActivity : AppCompatActivity() {
    var lastNumeric:Boolean=false
    var lastDecimal:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    fun onDigit(view: View){
        val result=findViewById<TextView>(R.id.result)
        result.append((view as Button).text)

        lastNumeric=true
    }
    fun onClr(view:View){
        val result =findViewById<TextView>(R.id.result)
        result.text=""
        lastNumeric=false
        lastDecimal=false
    }
    fun onDecimal(view:View){
        val result=findViewById<TextView>(R.id.result)
        if(lastNumeric && !lastDecimal){
            result.append(".")
            lastNumeric=false
            lastDecimal=true
        }
    }
    fun onOperator(view: View){
        val result=findViewById<TextView>(R.id.result)
        if(lastNumeric || (result.text.isEmpty() && (view as Button).text.startsWith('-')))
        {
            result.append((view as Button).text)
            lastNumeric=false
            lastDecimal=false
        }
    }
    private fun removeOneZero(txt: String):String{
        var result=txt
        if(txt.contains(".0"))
            result=txt.substring(0,txt.length-2)
        return result
    }
    private fun hasOperator(sign:Char):Boolean{
        if(sign=='/'|| sign=='-' || sign=='*' || sign=='+')
            return true
        return false
    }
    private fun precedence(i:Char):Int{
        if(i=='+' || i=='-')
            return 1
        else if(i=='/' || i=='*')
            return 2
        return 0
    }
    fun onEqual(view:View){
        val result=findViewById<TextView>(R.id.result)
        var resVal=result.text

        if(resVal.endsWith('+')|| resVal.endsWith('-')||resVal.endsWith('*')||resVal.endsWith('/'))
            resVal=resVal.substring(0,resVal.length-1)

        var prefix=""
        if(resVal.startsWith('-')){
            prefix="-"
            resVal=resVal.substring(1)
        }
        val splitVal= resVal.split('+','-','*','/').toMutableList()

        if(prefix.isNotEmpty())
            splitVal[0]="-"+splitVal[0]

        var x=0
        var equals=0.00

        val operands=Stack<Double>()
        operands.push((splitVal[x]).toDouble())

        val operators=Stack<Char>()
        for(i in resVal){
            if(hasOperator(i)){
                if(operators.isEmpty() || precedence(i)>precedence(operators.Top()))
                    operators.push(i)
                else if(precedence(i)<=precedence(operators.Top())){
                    while(!operators.isEmpty() && precedence(i)<=precedence(operators.Top() )){
                        val sign=operators.pop()
                        val two=operands.pop()
                        val one=operands.pop()

                        equals=evaluate(one,two,sign)
                        operands.push(equals)
                    }
                    operators.push(i)
                }
                x+=1
                operands.push(splitVal[x].toDouble())
            }
        }
        while(!operators.isEmpty())
        {
            val two=operands.pop()
            val one=operands.pop()
            val sign=operators.pop()

            equals=evaluate(one ,two ,sign )
            operands.push(equals)
        }
        result.text=removeOneZero((operands.pop()).toString())
    }
    private fun evaluate(x:Double,y:Double,sign:Char):Double{
        var res=0.0
        when(sign){
            '+' -> res=x+y
            '-' -> res=x-y
            '*' -> res=x*y
            '/' -> res=x/y
        }
        return res
    }
    /*
    fun onEqual(view: View){
        val tvInput=findViewById<TextView>(R.id.result)
        var tvValue=tvInput.text.toString()
        var prefix=""
        if(tvValue.startsWith("-")) {
            prefix = "-"
            tvValue = tvValue.substring(1)
        }
            if(tvValue.contains("-")) {
                val splitValue=tvValue.split("-")
                var one=splitValue[0]
                val two=splitValue[1]

                if (prefix.isNotEmpty()) {
                    one=prefix+one
                }
                tvInput.text=removeOneZero((one.toDouble()-two.toDouble()).toString())
            }
            else if(tvValue.contains("*")) {
                val splitValue=tvValue.split("*")
                var one=splitValue[0]
                val two=splitValue[1]

                if (prefix.isNotEmpty()) {
                    one=prefix+one
                }
                tvInput.text=removeOneZero((one.toDouble()*two.toDouble()).toString())
            }
            else if(tvValue.contains("+")) {
                val splitValue=tvValue.split("+")
                var one=splitValue[0]
                val two=splitValue[1]

                if (prefix.isNotEmpty()) {
                    one=prefix+one
                }
                tvInput.text=removeOneZero((one.toDouble()+two.toDouble()).toString())
            }
            else if(tvValue.contains("/")) {
                val splitValue=tvValue.split("/")
                var one=splitValue[0]
                val two=splitValue[1]

                if (prefix.isNotEmpty()) {
                    one=prefix+one
                }
                tvInput.text=removeOneZero((one.toDouble()/two.toDouble()).toString())
            }
    }
    private fun hasOperator(value:String):Boolean{
        return if(value.startsWith("-")){
            false
        }
        else {
            value.contains("+") || value.contains("-") || value.contains("/")
                    ||value.contains("*")

        }
    }*/
}