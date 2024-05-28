import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amory.departmentstore.databinding.ItemRevenueByCategoriesBinding
import com.github.mikephil.charting.data.PieEntry
import java.text.NumberFormat
import java.util.Locale

class RevenueByCategoriesAdapter(
    private val entries: List<PieEntry>,
    private val colors: List<Int>
) : RecyclerView.Adapter<RevenueByCategoriesAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemRevenueByCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: PieEntry, color: Int) {
            binding.txtNameCategory.text = entry.label
            binding.txtTotalMoney.text = formatAmount(entry.value)
            binding.imvColorRevenue.setBackgroundColor(color)
        }
    }
    private fun formatAmount(amount: Float): String {
        val number = amount.toLong()
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        return "${formatter.format(number)}đ"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemRevenueByCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(entries[position], colors[position])
    }
}