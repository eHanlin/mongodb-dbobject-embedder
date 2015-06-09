# !!尚未完成 (!! Not Ready)

# 目標規格

## DSL語法
```
//用傳入的 String List 中的 Task id 填充 Task
@findOneById [db=user, coll=Task, projection={"unit":1}]
{

  @findOneById {db=info, coll=Unit, projection={"createDate":0}}
  unit : {

    @findOneById [coll=Knowledge]
    knowledge : {
      
      @findOneById <>
      subject : true
      
    }

    @findOne <query={"_id":@}>
    subject : true

    //虛擬變數，先 distinct 取得 subject 的 id ， 再用 findOneById 填充 subject 的詳細資訊
    @distinct (coll=Video, key=subject, query={"knowledge":{"$in":@[knowledge]}})
    @findOneById [coll=subject]
    videoSubject : true

  }

}
```

## 支援的操作
### @find
* db
* coll
* projection
* query

### @findOne
* db
* coll
* projection
* query

### @findOneById
* db
* coll
* projection

### @distinct
* db
* coll
* key

### @count
* db
* coll
* query

### @aggregate
* db
* coll
* pipelines


##作用域

* ( )  父層為 @
* < >  屬性值本身為 @
* [ ]  屬性值底下的每個 item 為 @

若屬性值本身不是集合，則 [] 和 {} 的 @ 指向同一個物件

```
{

  subject : "PC"

  video : [ "V_1" , "V_2" ]

  user : [ { _id : "U_1" , name : "N_1" } , { _id : "U_2" , name : "N_2" } ]

  link : { url : "URL" , display : "LINK" , next : { url : "NEXT_URL" , display : "NEXT" } }

}
```

作用域同時會影響各 action 的處理方式，
以上面的資料為基準

```
@findOne (query={"_id":"@"})
video

coll.findOne({"_id":{"subject":"PC","video":["V_1","V_2"],"user":[{"_id":"U_1","name":"N_1"},{"_id":"U_2","name":"N_2"}],"link":{"url":"URL","display":"LINK","next":{"url":"NEXT_URL","display":"NEXT"}}}})
```

```
@findOne <query={"_id":"@"}>
video

coll.findOne({"_id":["V_1","V_2"]})
```

```
@findOne [query={"_id":"@"}]
video

[
  coll.findOne({"_id":"V_1"}),
  coll.findOne({"_id":"V_2"})
]
```

```
@findOne [query={"_id":"@"}]
link

{
  url : coll.findOne({"_id":"URL"}),
  display : coll.findOne({"_id":"LINK"}),
  next : coll.findOne({"_id":{"url":"NEXT_URL","display":"NEXT"}})
}
```
