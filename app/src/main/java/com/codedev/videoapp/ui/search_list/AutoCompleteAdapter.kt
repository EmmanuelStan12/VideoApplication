import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.codedev.videoapp.databinding.LayoutAutoCompleteBinding
import com.codedev.videoapp.ui.search_list.AutoCompleteItem

class AutoCompleteAdapter : RecyclerView.Adapter<AutoCompleteAdapter.AutoCompleteViewHolder>(){

    private val itemCallback = object: DiffUtil.ItemCallback<AutoCompleteItem>() {
        override fun areItemsTheSame(
            oldItem: AutoCompleteItem,
            newItem: AutoCompleteItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AutoCompleteItem,
            newItem: AutoCompleteItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    var itemClickListener: AutoCompleteItemClickListener? = null

    val list = AsyncListDiffer(this, itemCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoCompleteViewHolder {
        return AutoCompleteViewHolder(
            LayoutAutoCompleteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AutoCompleteViewHolder, position: Int) {
        val item = list.currentList[position]
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return list.currentList.size
    }

    inner class AutoCompleteViewHolder(private val binding: LayoutAutoCompleteBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AutoCompleteItem) {
            binding.textSuggestion.text = item.text
            binding.deleteIcon.setOnClickListener {
                itemClickListener?.onDeleteItem(item)
            }
        }
    }
}

interface AutoCompleteItemClickListener {
    fun onItemClick(item: AutoCompleteItem)
    fun onDeleteItem(item: AutoCompleteItem)
}
