package com.whilerain.tinychartimport org.junit.Testclass TimeSeriesProgressViewTest{    @Test    fun testProgress(){        println(myfun(0, 200))        println(myfun(1, 200))        println(myfun(50, 200))        println(myfun(51, 200))        println(myfun(150, 200))    }    fun myfun(index: Int, size: Int): Int{        return Math.min(            index - ((100 - 1) / 2f).toInt(),            Math.max(0, (size - 1 - 100))        )    }}