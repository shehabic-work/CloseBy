CloseBy
=======

Place any element in android beside/above/below existing element with a lot of cool features


How to use it
=======

        CloseBy.Builder builder = new CloseBy.Builder(this)
                .setPosition(CloseBy.POSITION_TOP_RIGHT)
                .setSourceView(existing_view_of_any_type)
                .setCloseBy(R.layout.new_view_to_be_places, this)
                .setMargin(5, -3, 0, 0);
        CloseBy cb = builder.build();
        
        cb.show();
        
        cb.hide();
        
