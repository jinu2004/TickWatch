package dashboard

import dashboard.query.RealtimeEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RealtimeEventBus {

    private val _events = MutableSharedFlow<RealtimeEvent>(extraBufferCapacity = 1000, replay = 1)
    val events = _events.asSharedFlow()

    init {
        println("BUS INStance" + this.hashCode())
    }


    fun publish(event: RealtimeEvent){
        println("publishing")
        _events.tryEmit(event)
    }


}