#!/bin/bash
D=app/src/main/res/drawable
V='<vector xmlns:android="http://schemas.android.com/apk/res/android" android:width="24dp" android:height="24dp" android:viewportWidth="24" android:viewportHeight="24">'
P='android:fillColor="?attr/colorOnSurface"'

write(){ echo -e "$V\n  <path $P android:pathData=\"$2\"/>\n</vector>" > $D/$1.xml; }

write bottom_sheet_handle "M9,12.5h6c.83,0,.83-1,0-1H9c-.83,0-.83,1,0,1z"
write ic_arrow_back "M20,11H7.83l5.59-5.59L12,4l-8,8 8,8 1.41-1.41L7.83,13H20v-2z"
write ic_arrow_forward "M12,4l-1.41,1.41L16.17,11H4v2h12.17l-5.58,5.59L12,20l8-8z"
write ic_bookmark_add "M17,11v6l-5-2.18L7,17V3h7V1H7C5.9,1,5,1.9,5,3v18l7-3,7,3V11h-2zM20,7h-2V5h-2V3h2V1h2v2h2v2h-2v2z"
write ic_bookmarks "M17,3H7C5.9,3,5,3.9,5,5v16l7-3,7,3V5C19,3.9,18.1,3,17,3z"
write ic_close_all "M5,13h14v-2H5v2zM3,17h14v-2H3v2zM5,7v2h14V7H5z"
write ic_code "M9.4,16.6L4.8,12l4.6-4.6L8,6l-6,6,6,6,1.4-1.4zM14.6,16.6l4.6-4.6-4.6-4.6L16,6l6,6-6,6-1.4-1.4z"
write ic_create_new_folder "M20,6h-8l-2-2H4C2.89,4,2.01,4.89,2.01,6L2,18c0,1.11.89,2,2,2h16c1.11,0,2-.89,2-2V8C22,6.89,21.11,6,20,6zM19,14h-3v3h-2v-3h-3v-2h3V9h2v3h3v2z"
write ic_desktop "M21,2H3C1.9,2,1,2.9,1,4v12c0,1.1.9,2,2,2h7l-2,3v1h8v-1l-2-3h7c1.1,0,2-.9,2-2V4C23,2.9,22.1,2,21,2zM21,16H3V4h18v12z"
write ic_find_in_page "M20,19.59V8l-6-6H6C4.9,2,4.01,2.9,4.01,4L4,20c0,1.1.89,2,1.99,2H18c.45,0,.85-.15,1.19-.4l-4.43-4.43C13.96,17.69,13.02,18,12,18c-2.76,0-5-2.24-5-5s2.24-5,5-5,5,2.24,5,5c0,1.02-.31,1.96-.83,2.75L20,19.59zM9,13c0,1.66,1.34,3,3,3s3-1.34,3-3-1.34-3-3-3S9,11.34,9,13z"
write ic_grid "M3,3v8h8V3H3zM9,9H5V5h4v4zM3,13v8h8v-8H3zM9,19H5v-4h4v4zM13,3v8h8V3h-8zM19,9h-4V5h4v4zM13,13v8h8v-8h-8zM19,19h-4v-4h4v4z"
write ic_mic "M12,14c1.66,0,2.99-1.34,2.99-3L15,5c0-1.66-1.34-3-3-3S9,3.34,9,5v6c0,1.66,1.34,3,3,3zM17.3,11c0,3-2.54,5.1-5.3,5.1S6.7,14,6.7,11H5c0,3.41,2.72,6.23,6,6.72V21h2v-3.28c3.28-.48,6-3.3,6-6.72h-1.7z"
write ic_pause "M6,19h4V5H6v14zM14,5v14h4V5h-4z"
write ic_pin "M16,9V4h1c.55,0,1-.45,1-1s-.45-1-1-1H7c-.55,0-1,.45-1,1s.45,1,1,1h1v5c0,1.66-1.34,3-3,3v2h5.97v7l1,1,1-1v-7H19v-2C17.34,12,16,10.66,16,9z"
write ic_restore "M13,3C8.03,3,4,7.03,4,12H1l3.89,3.89.07.14L9,12H6c0-3.87,3.13-7,7-7s7,3.13,7,7-3.13,7-7,7c-1.93,0-3.68-.79-4.94-2.06l-1.42,1.42C8.27,19.99,10.51,21,13,21c4.97,0,9-4.03,9-9S17.97,3,13,3zM12,8v5l4.28,2.54.72-1.21-3.5-2.08V8H12z"
write ic_screenshot "M17,1.01L7,1C5.9,1,5,1.9,5,3v18c0,1.1.9,2,2,2h10c1.1,0,2-.9,2-2V3C19,1.9,18.1,1.01,17,1.01zM17,19H7V5h10v14z"
write ic_shield_secure "M12,1L3,5v6c0,5.55,3.84,10.74,9,12,5.16-1.26,9-6.45,9-12V5L12,1zM10,17l-4-4,1.41-1.41L10,14.17l6.59-6.59L18,9l-8,8z"
write ic_ai_avatar "M12,2C6.48,2,2,6.48,2,12s4.48,10,10,10,10-4.48,10-10S17.52,2,12,2zM12,5c1.66,0,3,1.34,3,3s-1.34,3-3,3-3-1.34-3-3S10.34,5,12,5zM12,19.2c-2.5,0-4.71-1.28-6-3.22.03-1.99,4-3.08,6-3.08,1.99,0,5.97,1.09,6,3.08-1.29,1.94-3.5,3.22-6,3.22z"

echo "Done — all icons written"
