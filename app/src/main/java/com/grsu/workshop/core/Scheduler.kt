package com.grsu.workshop.core

import io.reactivex.rxjava3.core.Scheduler

class Scheduler(val title: String) : Scheduler() {
    override fun createWorker(): Worker {
        return SchedulerWorker(title)
    }
}