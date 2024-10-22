## TaskTrain by Calendify

## Table of Contents
1. [Overview](#Overview)
2. [Description](#Description)
3. [App Evaluation](#App-Evaluation)
4. [Schema](#Schema)
5. [Technology Used](#Technology-Used)



### Overview
TaskTrain is a productivity app meant to help organize your daily schedule and routines. 
•	The major features included are creating alarms, calendar integration, grouping alarms with other users, and social friend integration. To accomplish group alarms users can add/remove friends and then request users to set the same alarm.
•	Technologies: Firebase, Android Studio, GitHub, RoomDB, SQL, and Java.

### Description
When the user first opens the application they are greeted with the Splashscreen showing out the icon and splash art. Then they are navigated to the main ToDo fragment which shows users their upcoming ToDo items. When first opening the app the user is logged into an anonymous user account which can use the majority of the ToDo item features, such as setting alarms. However, an anonymous user cannot add friends, and interact with the social features of the application. Below are two gifs showing the SplashScreen activity and the anonymous user functionality.
<br />
<img src='https://i.imgur.com/fJW2WfX.gif' width='200' alt='Video Walkthrough' /> <img src='https://i.imgur.com/09fAJ1P.gif' width='200' alt='Video Walkthrough' />

<br />

As a user (anonymous or logged in) you can create ToDo items specifically designated with a Name, Location, Description, Category, Time, Date, Alarm, and Recurring Times. If an Alarm is to be set the user is required to enter a Time and a Date in the creation screen. Alarm Buddies allow the user to add friends to alarms they create for group activities, however, this feature is only possible for logged-in users. Below are two gifs showing ToDo creation with and without an Alarm.
<br />

<img src='https://i.imgur.com/z7Y3T87.gif' width='200' alt='Video Walkthrough' />  <img src='https://i.imgur.com/caXeU3y.gif' width='200' alt='Video Walkthrough' />

<br />

If a ToDo item is linked with a specific alarm then once the time and date match the specified values the user will receive a notification that the alarm arrived. A sound will play and once the notification is clicked on the user can choose to dismiss the alarm. If the alarm is set as recurring then the alarm will continue to occur at the designated time and days until the alarm is deleted. Below is a gif showing a normal alarm being displayed to the user. 
<br />
 
<img src='https://i.imgur.com/EiRMkzF.gif' width='200' alt='Video Walkthrough' />

<br />

<br />

A user can also delete or complete an alarm before or after the specific ToDo item activates the alarm. Deletion can be done via swiping right, which deletes the alarm from the database, or by long-pressing on a Todo item. In the action bar menu, a user can choose to select all items or select specific items. Once all the items are selected the user can delete all selected items by picking the trash icon. Completion is similar to deletion except it displays a specific Toast message which is meant to be more rewarding for the user. 
<br />
 
<img src='https://i.imgur.com/RZkntMS.gif' width='200' alt='Video Walkthrough' /> <img src='https://i.imgur.com/0pEbBEN.gif' width='200' alt='Video Walkthrough' />

<br />

After creating alarms the user can choose to sort and update alarms after creation. The user can use a Search View to enter characters and find a ToDo item by the Name field of an item. This can also be paired with Sort By Alphabet + Category. A user can also choose to Update an existing task just by clicking on the item. The preset data will be already sent to the EditTask screen for the user to see. Below are two gifs showing the Sorting features and Update functionality. 
<br />

<img src='https://i.imgur.com/UsGMpxH.gif' width='200' alt='Video Walkthrough' />  <img src='https://i.imgur.com/0vuq0Qj.gif' width='200' alt='Video Walkthrough' />

<br />

Another section of the application is the Calendar screen for the user to see all the upcoming alarms set for the entire month. The user can choose to flip between any past or future months. Once a day is selected the user will be displayed all the ToDo items for that specific day. From the calendar screen, the user can also choose to create an alarm for a specific day. The database is linked with the ToDo Fragment screen.
<br />

<img src='https://i.imgur.com/caZYnzB.gif' width='200' alt='Video Walkthrough' /> 

<br />

An anonymous user can choose to create an account to unlock the custom feature only accessible by users that are logged into the application with an account. Account creation is easy and simple, only requiring an Email, Name, and Password. The display is a non-unique way of identification between users. Once an account is created the user can also choose to log in or log out from the app. Logging in is simple and only requires an email and password. First, you enter an email, and if an account is associated with the email it will ask for a password. Else the user will be prompted to create an account with the email entered. Below are gifs displaying account creation, logging in, and logging out.
</br>

<img src='https://i.imgur.com/u8uWelk.gif' width='200' alt='Video Walkthrough' /> <img src='https://i.imgur.com/sgZGRTf.gif' width='200' alt='Video Walkthrough' /> <img src='https://i.imgur.com/881VXmx.gif' width='200' alt='Video Walkthrough' />

</br>

Once logged into an account the user can choose to edit their account preferences. The user can change their username and profile picture. Profile pictures are picked from a given set of images. A user's email is unable to be altered and is the unique identifier for accounts. Users can request password changes via Password Reset emails.

</br>

<img src='https://i.imgur.com/NGcoBDg.gif' width='200' alt='Video Walkthrough' />
]
</br>

One important and custom feature in the app for logged-in users is the ability to add other users as friends. Once a user is added as a friend they can be selected as alarm buddies when creating alarms. When a user sends a friend request the requested user receives a notification and can choose to accept or decline the request. Users can also delete friends and have the changes reflected on the friend account as well. Deleting friends is simple and only requires a user to select them from the presented list. 
</br>

<img src='https://i.imgur.com/okaACZj.gif' width='400' alt='Video Walkthrough' /> 

<img src='https://i.imgur.com/r8sn8gT.gif' width='400' alt='Video Walkthrough' />
</br>










<img src='https://i.imgur.com/KLoVv5z.png' width='200' height='400' alt='Video Walkthrough' /> <img src='https://i.imgur.com/0MBmjvQ.png' width='200' height='300' alt='Video Walkthrough' />

GIF created with [LiceCap](https://www.cockos.com/licecap/).



### App Evaluation
- **Category:** Productivity
- **Mobile:** Mobile is essential for users to easily create new events and to-do items. The system alarm can be used with the events and tasks that are created.
- **Story:** Creates a system for any users seeking to improve productivity to stay on track and build good habits.
- **Market:** Anyone who looks to improve their productivity and habits. The app provides a way for people to organize their life. Our target could directly be for students.
- **Habit:** Users would use the app daily in order to stay on track with assignments, meetings, and other important tasks.
- **Scope:** V1 would allow users to create events and to-dos that can be edited/completed. V2 would incorporate setting the alarm for events that would notify users.


### Schema
To-Do Object
| Property      | Type | Description |
| ----------- | ----------- | ------------ |
| Name | String  | Name of the to-do item. |
| Description | String | Additional details about the to-do item. |
| Date | String | The calendar date that this to-do item is supposed to happen. |
|Time |Java.time Object | The time at which this to-do item will begin. |
|Status | boolean | Whether or not the to-do item has been completed. |
|HasAlarm | boolean | Whether the user has designated an alarm for this to-do item or not. |
|MinutesBefore | number | How many minutes before the to-do item an alarm is supposed to go off. |

Event
|Property |Type |Description|
|---|---|---|
|Name | String | Name of the event|
|Location | String | Where the event is occuring. |
|Description | String | Additional information about the event.|
|Date| String | When the event(s) is supposed to occur. |
|Time | Java.Time| When the event will occur. |
|Recurrence | String | Which days of the week this event will occur on.|
|HasAlarm | boolean | Whether the user has designated an alarm for this event or not. |
|MinutesBefore | number | How many minutes before the event an alarm is supposed to go off. |

Alarm 
|Property |Type |Description|
|---|---|---|
|Name | String | The name of the alarm. |
|Time | Java.time | When the alarm should go off. |
|Recurrence | String | Which days of the week the alarm should ring. |

### Technology Used

Room Persistence Library, AlarmManager, Jinatonic Confetti Library, Alerter Library, Styleable Toast Library

Graphic Assets Produced by: [Marcela Mayor](https://www.instagram.com/marcy_mayor/)


