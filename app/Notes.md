# Notes 
## Requirements
    * Functional:
        - One time Location vs Flow. I had an option to create use case that requests user location and returns Location class,
        but I decided that if I implement usecase that returns flow i can show some important edge cases
        like user denying permissions while app in background. 
        - I decided to keep error handling out of scope, because it can make app quite complex, for example: 
            - Do we want to show error screen to user when we failed to get user location update? (probably not and we want to show stale data instead)
            - How we handle cases when user scroled list and we got updated data? there are different ways to handle these cases, depends on bussines requirement
            - Do we want to retry if getVenues failed? 
        - Implemeted reloading venues when user moved more than 20 from previos point 
        
    * Non functional:
        - Cache venues (out of scope)
        - No effort implementing ui, because it is not focus of the task
        - We can discuss location usecase setup
        - We can discuss tech stack, like di vs service locator and etc