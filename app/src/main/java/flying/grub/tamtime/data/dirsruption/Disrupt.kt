package flying.grub.tamtime.data.dirsruption

import java.util.Calendar

import flying.grub.tamtime.data.Data
import flying.grub.tamtime.data.map.Line

data class Disrupt(val line: Line, val startDate: Calendar,
                   val endDate: Calendar, val description: String,
                   val title : String) {

    override fun toString() : String {
        return title + " " +  line.id
    }
}
