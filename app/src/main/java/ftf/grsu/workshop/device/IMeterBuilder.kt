package ftf.grsu.workshop.device

interface IMeterBuilder{

    val address: String

    val title: String

    val meter: Meter
}