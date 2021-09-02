# TWer
## Travel Planner Android APP
- ### The following are the features of my project:
1. [Search for Attractions](#attractions)
2. [Collect Attractions](#attractions)
3. [Trip Planning](#schedule)
4. [Travel Notes](#travel-notes)
5. [Account](#menu)
- ### Screenshots
    - #### Menu
      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-141358.png" width="270" height="480">

    - #### Attractions
      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-212858.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-212644.png" width="270" height="480">
    
      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-213859.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-213947.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-213953.png" width="270" height="480">

    - #### Schedule
      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-141338.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot%20(2019%E5%B9%B45%E6%9C%8810%E6%97%A5%20%E4%B8%8A%E5%8D%8810_35_43).png" width="270" height="480">

      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-213847.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-221653.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-221709.png" width="270" height="480">

    - #### Travel Notes
      <img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-212618.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-210732.png" width="270" height="480">&nbsp;&nbsp;&nbsp;<img src="https://github.com/HC-TW/TWer/blob/master/Screenshots/Screenshot_20190507-210922.png" width="270" height="480">

## Project Requirements
- ### System Requirements
  - Android Version 5.0 or higher
- ### User Requirements
  1. Compatible on various mobile phones and tablets
  2. User-friendly interface
  3. There is a prompt text to know what to enter
  4. The telephone and website in the attractions information can be directly called and linked
  5. The established itinerary can be browsed offline
  6. APP data can be backed up to the cloud
- ### User Interface Requirements
  - #### Symbolic icon
    - Assign an icon to each link as a preview of the content
  - #### Menu and previous page
    - Each page must contain a menu icon or the previous page
  - #### Scalable
    - There must be space for new features or new links
  - #### Screen size
    - Should apply to all sizes
- ### Functional Requirements
  - #### Create itinerary
    1. Toolbar displays the name of the page, the return icon, and the creation text to indicate "Create"
    2. Show the prompt text in the first input field - "Please enter the name of the itinerary"
    3. Show the prompt text in the second input field - "Please select a travel date"
    4. The second input field is linked to the calendar for date selection
  - #### My itinerary (home page)
    1. Toolbar displays the page name, menu icon and create itinerary icon
    2. The itinerary list shows the cover image, name, and date of the itinerary
    3. The cover image of the itinerary is randomly generated or the user changes it to a photo in his own gallery
    4. itinerary cover links to the page for editting the itinerary
    5. Sideslip itinerary — display two buttons for copying or deleting itinerary
  - #### Search for attractions
    1. The Toolbar displays the page name and menu icon
    2. Show the prompt text in the input field - "Please enter the area or name of the attraction"
    3. Cancel button for clearing the input field
    4. When the input field changes, the corresponding search result will be generated
    5. The search result shows the name and address of the attraction
    6. The search result is linked to the attraction information page
    7. The maximum number of search results is five
    8. Only locations in Taiwan will appear
  - #### Edit itinerary
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
  - #### Collect attractions
    1. The Toolbar displays the page name and menu icon
    2. Collect specific attractions and store them in the favorite list for future use
    3. The pictures and names of the attractions are displayed in the favorite list
    4. In case there is no attractions picture, the built-in random picture will be displayed
    5. Collected attractions link to information pages
  - #### Attractions Information
    1. The Toolbar displays the name of the attraction and the return icon
    2. Tab title - "Detailed Information", "Map", and "Comment"
    3. "Detailed Information" shows the attraction photos, name, address, phone number, website, and rating
        - If it is a restaurant or needs tickets, the price level will also be displayed
        - In case there is no such information, "No such information" is displayed
        - Click the favorite icon to collect or remove the attraction
        - Tap a photo to browse more photos of attractions
    4. "Map" displays a map of the surrounding attractions
        - Mark the location of the attraction in the center
        - The button in the upper left corner turns the map to the north
        - The button in the upper right corner locates the current position
        - The button at the bottom right corner zooms the map size.
    5. "Comment" shows the author, content, and time of the attraction reviews (sorted by time)
  - #### Account
    1. The 1/3 part of the side-slide menu has a login button, which is changed to the user profile picture and account name after login
    2. Log in to the user's google account to obtain the user's cloud drive permissions
    3. The user's cloud drive automatically synchronizes the current APP data (itinerary content, favorite attractions)
  - #### Travel notes
    1. Toolbar on the homepage of Travel notes displays the page name and menu icon
    2. The homepage shows the list of counties and cities
    3. The list of counties and cities displays the names of counties and cities and their landscape pictures
    4. The list of counties and cities is linked to the list of travel notes in the area (page 2)
        - The Toolbar on the second page displays the name of the county and city and the return icon
        - The system searches for the county and city selected by the user to generate a travel list (current year)
        - The travel notes list shows the travel notes picture, name, and author
        - Travels pictures are randomly generated
        - The maximum number of travel notes is twenty
    5. The travel notes list links to the contents of the travel notes webpage (page 3)
        - The Toolbar on the third page displays the travel name and the return icon
        - There are some buttons at the bottom of the screen, previous page, refreshing, and searching for the attractions related to this travel note (users enter keywords by themselves, the maximum number of search results is ten)
