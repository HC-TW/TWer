# TWer
## Travel Planner Android APP
### The following are the features of my project:
1. Search for Attractions
2. Collect Attractions
3. Trip Planning
4. Travel Notes
5. Account
## Project Requirements
### System Requirements
Android Version 5.0 or higher
### User Requirements
1. Compatible on various mobile phones and tablets
2. User-friendly interface
3. There is a prompt text to know what to enter
4. The telephone and website in the attractions information can be directly called and linked
5. The established itinerary can be browsed offline
6. APP data can be backed up to the cloud
### User Interface Requirements
#### Symbolic icon
- Assign an icon to each link as a preview of the content
#### Menu and previous page
- Each page must contain a menu icon or the previous page
#### Scalable
- There must be space for new features or new links
#### Screen size
- Should apply to all sizes
### Functional Requirements
#### Create itinerary
1. Toolbar displays the name of the page, the return icon, and the creation text to indicate "Create"
2. Show the prompt text in the first input field - "Please enter the name of the itinerary"
3. Show the prompt text in the second input field - "Please select a travel date"
4. The second input field is linked to the calendar for date selection
#### My itinerary (home page)
1. Toolbar displays the page name, menu icon and create itinerary icon
2. The itinerary list shows the cover image, name, and date of the itinerary
3. The cover image of the itinerary is randomly generated or the user changes it to a photo in his own gallery
4. itinerary cover links to the page for editting the itinerary
5. Sideslip itinerary — display two buttons for copying or deleting itinerary
#### 搜尋景點
1. The Toolbar displays the page name and menu icon
2. Show the prompt text in the input field - "Please enter the area or name of the attraction"
3. Cancel button for clearing the input field
4. When the input field changes, the corresponding search result will be generated
5. The search result shows the name and address of the attraction
6. The search result is linked to the attraction information page
7. The maximum number of search results is five
8. Only locations in Taiwan will appear
#### 編輯行程
1. Toolbar displays the page name, return icon, edit mode, and map mode
2. According to the set date, generating that number of pages for itinerary planning 
3. Edit mode can change the itinerary cover picture, name, date and start and end time of the attraction, as well as add or delete the itinerary planning page
4. There are two ways to add attractions, through favorite attractions or search attractions
5. Attractions have been added link to the attraction information page
6. Swipe the attraction to show two buttons - copy attraction icon and delete attraction icon
7. Choose the mode of transportation between locations (walking, car, subway, bus), evaluate the time required and plan the route
8. Traffic patterns between locations link to the route page
9. The map mode indicates the relative position of the existing attractions on the map
10. Marked attraction link to the attraction information page
11. After users add attractions, if they don’t know how to arrange them, they can click the best sorting reference
#### 收藏景點
- Toolbar顯示頁面名稱與選單圖示。
- 收藏特定景點，儲存在收藏列表中，以方便日後使用。
- 收藏列表中顯示景點的圖片與名稱。
- 萬一沒有景點圖片，顯示內建隨機圖片。
- 收藏景點連結景點資訊頁面。
#### 景點資訊
- Toolbar顯示景點名稱與返回圖示。
- 分頁標題—「詳細資訊」、「地圖」、「評論」。
- 「詳細資訊」顯示景點相片、名稱、地址、電話、網站、評分。
- 如果是餐廳或是有門票，還會顯示價錢等級（200NTD為單位）。
- 萬一沒有該項資料顯示「無此資料」。
- 點擊收藏圖示，收藏或取消景點。
- 點選相片可以瀏覽更多景點相片。
- 「地圖」顯示景點周遭地圖。
- 中心標註景點位置。
- 左上角按鈕將地圖轉到北方。
- 右上角按鈕定位現在位置。
- 右下角按鈕縮放地圖大小。
- 「評論」顯示景點評論作者、內容、時間（依時間最近開始排序）。
#### 帳戶系統
- 側滑選單上1/3部分設置登入按鈕，登入後改為使用者頭像與帳戶名稱。
- 登入用戶的google帳號，取得用戶雲端硬碟權限。
- 用戶的雲端硬碟自動同步現在APP的資料（行程內容、收藏景點）。
- 雲端先下載到設備，存在一樣的檔案名稱詢問使用者是否覆寫到設備，不存在的新建檔案。
- 設備備份資料到雲端，覆寫一樣的檔案名稱，不一樣的新建檔案。
#### 遊記
- 遊記首頁Toolbar顯示頁面名稱與選單圖示。
- 首頁顯示縣市列表。
- 縣市列表顯示縣市名稱、風景圖。
- 縣市列表連結該區域遊記列表（第二頁）。
- 第二頁Toolbar顯示縣市名稱與返回圖示。
- 系統根據使用者選擇的縣市搜尋該縣市產生遊記列表（當年度）。
- 遊記列表顯示遊記圖片、名稱、作者。
- 遊記圖片隨機生成。
- 遊記列表上限為二十個。
- 遊記列表連結遊記網頁內容（第三頁）。
- 第三頁Toolbar顯示遊記名稱與返回圖示。
- 畫面下方有上一頁按鈕、重新整理和搜尋有關此遊記出現的景點（使用者自行輸入關鍵字，搜尋結果上限為十個）。
