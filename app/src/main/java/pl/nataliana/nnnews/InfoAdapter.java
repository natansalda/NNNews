package pl.nataliana.nnnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static pl.nataliana.nnnews.MainActivity.IS_SECTION_SEARCH;

public class InfoAdapter extends ArrayAdapter<Info> {

    public InfoAdapter(@NonNull Context context, @NonNull List<Info> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //Create a variable for the ViewHolder
        ViewHolder holder;

        //If the convertView does not exist, inflate it with the list_item.xml layout
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

            //Get the TextViews of list_item.xml and set them to the ViewHolder
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.item_title);
            holder.section = (TextView) convertView.findViewById(R.id.item_section);
            holder.date = (TextView) convertView.findViewById(R.id.item_date);
            convertView.setTag(holder);

        } else {

            //If the convertView already exists get its attributes
            holder = (ViewHolder) convertView.getTag();

        }

        final Info currentInfo = getItem(position);

        //Set the properties of the current object
        holder.title.setText(currentInfo.getTitle());
        holder.section.setText(currentInfo.getSection());
        holder.date.setText(currentInfo.getPublicationDate());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create an Intent with the list item URL to open it in a browser when clicking on the title of the item
                Uri url = Uri.parse(currentInfo.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, url);

                if (webIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    getContext().startActivity(webIntent);
                }
            }
        });

        holder.section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IS_SECTION_SEARCH = 1;
                Intent searchIntent = new Intent(getContext(), MainActivity.class);
                searchIntent.putExtra("IS_SECTION_SEARCH", IS_SECTION_SEARCH);
                searchIntent.putExtra("sectionToMonitor", currentInfo.getSection());
                getContext().startActivity(searchIntent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView title;
        TextView section;
        TextView date;
    }
}