# news-android
A simple news app that features :
- Display news banner related to user current location. 
- Display list of news 
- Search news based on user query
- Support the capability of offline reading

The project is a demo project to learn about the new paging library. It helps to load and display small chunks of data at a time. 
Loading partial data on demand that leads to the reduction of network bandwidth and system resources.

<img width="200" alt="Home" src="https://user-images.githubusercontent.com/26065617/62006506-6d026000-b16b-11e9-98c4-f4d149e2cde5.png">                           <img width="200" alt="Home_Search" src="https://user-images.githubusercontent.com/26065617/62006501-61af3480-b16b-11e9-8792-9cff0eb10e56.png">

### Data architecture
Network & database : Data flows from backend server, into an on-device database, and then to app's UI model.

### Stack
* Paging 
* Room
* Retrofit 2
* Dagger 2
* Data Binding
* RxJava2


