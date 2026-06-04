#!/data/data/com.termux/files/usr/bin/bash

D=app/src/main/res/drawable

make_alias() {
cat > "$D/$1.xml" <<EOT
<?xml version="1.0" encoding="utf-8"?>
<inset xmlns:android="http://schemas.android.com/apk/res/android">
    <bitmap android:src="@drawable/$2"/>
</inset>
EOT
}

make_alias ic_arrow_back ic_back
make_alias ic_arrow_forward ic_forward
make_alias ic_bookmark_add ic_bookmark
make_alias ic_bookmarks ic_bookmark
make_alias ic_create_new_folder ic_add
make_alias ic_shield_secure ic_shield
make_alias ic_code ic_dev_tools
make_alias ic_restore ic_history
make_alias ic_screenshot ic_share
make_alias ic_find_in_page ic_search
make_alias ic_desktop ic_home
make_alias ic_grid ic_tabs
make_alias ic_ai_avatar ic_ai
make_alias ic_pause ic_close
make_alias ic_pin ic_bookmark
make_alias ic_mic ic_search

cat > "$D/bottom_sheet_handle.xml" <<EOT
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <size android:width="36dp" android:height="4dp"/>
    <solid android:color="#808080"/>
    <corners android:radius="2dp"/>
</shape>
EOT

echo "done"
