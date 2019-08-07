package com.example.porterkillian.calculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast





class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var currentNum = ""
        var previousNum = ""
        var operationToUse =""
        numberButtonOne.setOnClickListener {
            currentNum = currentNum + "1"
            answer.text = currentNum
        }
        numberButtonTwo.setOnClickListener {
            currentNum = currentNum + "2"
            answer.text = currentNum
        }
        numberButtonThree.setOnClickListener {
            currentNum = currentNum + "3"
            answer.text = currentNum
        }
        numberButtonFour.setOnClickListener {
            currentNum = currentNum + "4"
            answer.text = currentNum
        }
        numberButtonFive.setOnClickListener {
            currentNum = currentNum + "5"
            answer.text = currentNum
        }
        numberButtonSix.setOnClickListener {
            currentNum = currentNum + "6"
            answer.text = currentNum
        }
        numberButtonSeven.setOnClickListener {
            currentNum = currentNum + "7"
            answer.text = currentNum
        }
        numberButtonEight.setOnClickListener {
            currentNum = currentNum + "8"
            answer.text = currentNum
        }
        numberButtonNine.setOnClickListener {
            currentNum = currentNum + "9"
            answer.text = currentNum
        }
        numberButtonZero.setOnClickListener {
            currentNum = currentNum + "0"
            answer.text = currentNum
        }
        plusButton.setOnClickListener {
            if(operationToUse == "") {
                operationToUse = "+"
                previousNum = currentNum
                currentNum = ""
                plusButton.setBackgroundColor(2)
            }
        }
        minusButton.setOnClickListener {
            if(operationToUse == ""){
                operationToUse = "-"
                previousNum = currentNum
                currentNum = ""
                minusButton.setBackgroundColor(290)
            }
        }
        var numOne = 0
        var numTwo = 0
        EqualsButton.setOnClickListener {
            try{
                numOne = currentNum.toInt();
                numTwo = previousNum.toInt();
            }catch(e: NumberFormatException) {
                println(e.printStackTrace())
            }
            if(operationToUse == "+"){
                var totalNum = numOne + numTwo
                answer.text = totalNum.toString()
                currentNum = totalNum.toString()
                operationToUse = ""
            }else if(operationToUse == "-"){
                var totalNum = numTwo - numOne
                currentNum = totalNum.toString()
                answer.text = totalNum.toString()
                operationToUse = ""
            }
        }
        EqualsButton.setOnLongClickListener{
            currentNum = ""
            previousNum = ""
            answer.text = "0"
            Toast.makeText(this, "Cleared", Toast.LENGTH_SHORT).show()
            true
        }



    }



}
