搜索相关的
curl -XPOST '192.168.187.130:9200/_bulk' --data-binary @books.json

{"index": {"_index": "library", "_type": "book", "_id": "1"}}
{"title": "All Quiet on the Western Front","otitle": "Im Westen nichts Neues","author": "Erich Maria Remarque","year": 1929,"characters": ["Paul Ba?umer", "Albert Kropp", "Haie Westhus", "Fredrich Mu?ller", "Stanislaus Katczinsky", "Tjaden"],"tags": ["novel"],"copies": 1, "available": true,"section" : 3}
{"index": {"_index": "library", "_type": "book", "_id": "2"}}
{"title": "Catch-22","author": "Joseph Heller","year": 1961,"characters": ["John Yossarian", "Captain Aardvark","Chaplain Tappman", "Colonel Cathcart", "Doctor Daneeka"],"tags": ["novel"],"copies": 6, "available" : false,"section" : 1}
{"index": {"_index": "library", "_type": "book", "_id": "3"}}
{"title": "The Complete Sherlock Holmes","author": "Arthur Conan Doyle","year": 1936,"characters":["Sherlock Holmes","Dr. Watson", "G. Lestrade"],"tags":[],"copies": 0, "available" : false, "section" : 12}
{"index": {"_index": "library", "_type": "book", "_id": "4"}}
{"title": "Crime and Punishment","otitle": "Преступлe?ние и наказa?ние","author": "Fyodor Dostoevsky","year": 1886,"characters": ["Raskolnikov", "Sofia Semyonovna Marmeladova"],"tags": [],"copies": 0, "available" : true}

//查询
GET /library/book/_search?q=title:catch
