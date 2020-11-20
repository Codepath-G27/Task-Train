# Calendify
# Group 27 Mobile App Development Document    

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Our app allows users to create events and to-do items that can be organized on a calendar and to-do list. Each event and to-do allows users to set the details, such as date, time, and other details. Users can evenset alarms to notify them of things that are coming up.

### App Evaluation
- **Category:** Productivity
- **Mobile:** Mobile is essential for users to easily create new events and to-do items. The system alarm can be used with the events and tasks that are created.
- **Story:** Creates a system for any users seeking to improve productivity to stay on track and build good habits.
- **Market:** Anyone who looks to improve their productivity and habits. The app provides a way for people to organize their life. Our target could directly be for students.
- **Habit:** Users would use the app daily in order to stay on track with assignments, meetings, and other important tasks.
- **Scope:** V1 would allow users to create events and to-dos that can be edited/completed. V2 would incorporate setting the alarm for events that would notify users.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User must be able to see a calendar that has all events and to-do items
* User must be able to create a to-do and event items and set item name, time, alarm
* User must be able to see relevant to-do items in a list
* User must be able to edit and remove items from the calendar
* User receives a daily notification stating what events/to-do items they have for the day
* User is able to adjust alarms, reminders through a settings page


**Optional Nice-to-have Stories**

* User can import other calendars via .ics files (for Canvas, Outlook, Google Calendar support)
* User is able to customize the calendar with color options
* User is able to add labels to to-do/event items
* User is able to add descriptions to alarms and to-do items. 
* User can see upcoming schedule via a widget for the lockscreen

### 2. Screen Archetypes

* Calendar Screen
   * Displays all events and to-do items
   * User can edit/delete an event/to-do item by tapping on it
* To-Do List Screen
   * User can view all upcoming events and to-doo items
   * User can tap a "plus/add" button in order to add a to-do item
* To-Do/Event Creation Screen
    * User can set/edit event/to-do item, classify item, and set an alarm 
* Settings Screen
    * User can adjust alarm settings, like how many minutes/hours before, alarm sound, alarm sound choice

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Calendar
* To-do List

**Flow Navigation** (Screen to Screen)

* Calendar
   * Create event
   * Click day
   * Swipe between months
   * Edit events
* To-do List
   * Create tasks
   * Check off tasks
   * Delete and edit tasks
* Create Event/To-do
   * Date
   * Time
   * Alarm setting
* Alarm
   * Scroll (hour, minute, AM/PM)
* Settings
    * Change settings

## Wireframes
<img src="wireframe.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 

| Property      | Type | Description
| ----------- | ----------- | ------------
| Header      | Title       |
| Paragraph   | Text        |
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
