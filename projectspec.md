
## Product Spec

### 1. User Stories (Required and Optional)



## Schema 
User Stories

- [X]  Unique UI design with icon and splashScreen
- [X]  User must be able to create a to-do and event items and set item name, time, alarm
- [X]  User must be able to see relevant to-do items in a list
- [X]  User must be able to edit and remove items from the list of task
- [X]  User receives a notification at a designated time
- [X]  User is able to set alarms/reminders
- [X]  Reminders activate at the given time notifying the user
- [X]  Synch between task and reminder creation

Here's an update on our Unit 11 progress: 
### Models
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

### Networking
* Calendar Screen
  * (Read/GET) All events within the given month
  * (Create/POST) Creating a new event/to-do item
 * To-do/Event View 
    * (Read/GET) All upcoming events/to-do items
    * (Create/POST) Creating an event/to-do item
    * (Delete) Delete an event/to-do item
 * Alarm Screen
    * (Read/GET) All upcoming alarms
    * (Create/POST) Creating an alarm with its recurrence
    * (Delete) Delete an alarm
