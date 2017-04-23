package com.fern.yatranslater.db;

/**
 * Created by Andrey Saprykin on 16.04.2017.
 */

public class YaTable {
    public static final String table_name = "hf_table";

    private StringBuffer sb;

    private final String create_id = "id integer primary key autoincrement";
    private final String comma = ", ";
    private final String type_text = " text";
    private final String type_boolean = " boolean";

    public final String id = "id";
    public final String s_text = "source_text";
    public final String t_text = "translate_text";
    public final String f_item = "favorite_item";

    public YaTable() {
        if (sb == null) {
            this.sb = new StringBuffer();
        } else {
            sb.delete(0, sb.length());
        }
    }

    public String sqlCreateTable(){
        sb.append("create table if not exists ");
        sb.append(table_name);
        sb.append(" (");
        sb.append(create_id).append(comma);
        sb.append(s_text).append(type_text).append(comma);
        sb.append(t_text).append(type_text).append(comma);
        sb.append(f_item).append(type_boolean);
        sb.append(")");
        return sb.toString();
    }
}
