package eu.rampsoftware.recyclerbinding.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableList;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.ref.WeakReference;

/**
 * Created by Rafal on 2016-02-20.
 */
public class SimpleBindingListAdapter<T> extends RecyclerView.Adapter<SimpleBindingListAdapter.BindingHolder>
        implements Filterable {

    protected ObservableList<T> mItems;
    private ObservableList<T> allItems;
    private final int mBindingVariableId;
    private final int mRowLayoutResId;
    private final OnBindingListChangedCallback mOnListChangedCallback;
    private FilterI filterI;

    public SimpleBindingListAdapter(final ObservableList<T> items, final int bindingVariableId, final int rowLayoutResId) {
        mItems = items;
        allItems = items;
        mBindingVariableId = bindingVariableId;
        mRowLayoutResId = rowLayoutResId;
        this.mOnListChangedCallback = new OnBindingListChangedCallback(this);
        mItems.addOnListChangedCallback(this.mOnListChangedCallback);
    }

    public SimpleBindingListAdapter(final ObservableList<T> items, final int bindingVariableId,
                                    final int rowLayoutResId, FilterI filterI) {
        this(items, bindingVariableId, rowLayoutResId);
        this.filterI = filterI;
    }

    public T getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public BindingHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mRowLayoutResId, parent, false);
        BindingHolder holder = new BindingHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BindingHolder holder, final int position) {
        T item = mItems.get(position);
        holder.binding.setVariable(mBindingVariableId, item);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public Filter getFilter() {
        return new SimpleFilter();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(View rowView) {
            super(rowView);
            binding = DataBindingUtil.bind(rowView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }
    }

    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ObservableList<T> items = filterI.performFiltering(constraint, allItems);
            FilterResults filterResults = new FilterResults();
            filterResults.count = items.size();
            filterResults.values = items;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItems = (ObservableList<T>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface FilterI<T> {
        ObservableList<T> performFiltering(CharSequence constraint, ObservableList<T> mItems);
    }
}
