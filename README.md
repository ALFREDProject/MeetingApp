# ALFRED User Story 078

## Found bugs

- User can add same contact multiple times to the invited list
- If users first choose contacts first and create a meeting, they won't be redirected to the main activity
- Meeting datetimes are not displayed correctly in the main activity (probably a convert error from db to text)

## As an older person I would like to use ALFRED to organize a meeting with group of friends at a certain place

This user story has to be implemented as an Android-App, using Alfred Android library 

**PersonalAssistantShared-debug.aar**

The final version has to use CADE (Context Aware Dialog Engine), means interaction is voice-based only. But development will be incremental, so we will start with implementing a GUI.

### Flow of this app:

1. With starting the app, user will be asked for kind of meeting. Attributes:
    - Date/time
    - Place
    - Duration
2. Ask user, who should be invited
    - Request list of contacts from ALFRED Personal-Manager contact-information with attribute “Relation to user” = Friend.
    - Show the found friends with selection field, so that user can select friends
    - Having selected the friends, ask user, whether contacts from phone’s contact-list should be invited
    - If yes, display the contact-names, so that user can select friends from this list
3. Last action: send a notification to selected friends:
    - If mobile-phone number is available: send an SMS
    - If no mobile-phone number is available, but eMail address, send an eMail
    - If a friend doesn’t have mobile-phone number nor eMail address, inform user about those friends, having no corresponding contact-info
    - Message should contain: “I would like to meet you and friends at date/time. Meeting place: Place-attribute”. Please give me a notification, if you can join our meeting or not”

### CADE-Integration is mandatory

We need to define a DDD (Dialog Domain Description), which handles at least two actions:

1. Start app
2. Ask user for Date/time, place

### System Requirements:

1. App has to be registered in Alfred ecosystem
2. App has to register with Personal Assistant App (PA) (see Technical spec D2.5 Chapter 4.3.4.1)
3. App has to use permissions (see Technical spec D2.5 Chapter 4.3.4.3)
4. App has to communicate PA for getting data from Personalization manager (via Alfred library?)

### DDD (Dialog Domain Description)

U > I want to meet with friends

Alternatives: (U > setup a meeting with friends)

S > When do you want to have a meeting with friends? Which day as number)?

U > 10

S > which month?

U > 4    (we are assuming this year, if month is >= actual month; if < actual month -> next year)

S > what hour?

U > 17

S > what minute?

U > 0

S > Date and time set to hh:mm DD:MM:YYY

S > where do you want to meet your friends?

U > at “location” (which has to be translated to a plane text)
