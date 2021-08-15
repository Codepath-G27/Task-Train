## TaskTrain by Calendify

## Table of Contents
1. [Overview](#Overview)
2. [App Evaluation](#App-Evaluation)
3. [Schema](#Schema)
4. [Technology Used](#Technology-Used)



### Overview
TaskTrain is a productivity app meant to help organize your daily schedule and routines. In this app, the user can create Todo items, as well as set an alarm either associated with a task or not. 



<img src='https://i.imgur.com/fJW2WfX.gif' width='400' alt='Video Walkthrough' />  <img src='https://i.imgur.com/09fAJ1P.gif' width='600' alt='Video Walkthrough' />
<img src='https://i.imgur.com/z7Y3T87.gif' width='800' alt='Video Walkthrough' />  <img src='https://i.imgur.com/caXeU3y.gif' width='1000' alt='Video Walkthrough' />


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


