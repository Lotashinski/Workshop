package com.grsu.workshop.core

import android.util.Log
import java.util.concurrent.ThreadFactory

class ThreadFactory(val prefix: String): ThreadFactory {

    private object Inner {
        @Volatile
        var counter = 0L;
    }

    override fun newThread(r: Runnable): Thread {
        val counter = ++Inner.counter
        val thread = Thread{
            Log.d("t_factory", "run $prefix [$counter]")
            r.run()
            Log.d("t_factory", "close $prefix [$counter]")
        }
        thread.name = prefix + "_$counter"
        Log.d("t_factory", "create thread $prefix [$counter]")
        return thread
    }
}