# !!尚未完成 (!! Not Ready)

# 目標規格

## DSL語法
```
//用傳入的 String List 中的 Task id 填充 Task
@findOneById [db=user, coll=Task, projection={unit:true}]
<

  @findOneById [db=info, coll=Unit, projection={createDate:0}]
  unit <

    @findOne <query={_id:@}>
    subject

    @distinct (coll=Video, key=subject, query={subject:'PC',knowledge:{$in:@.knowledge}})
    @findOneById [coll=subject]
    videoSubject
    
    @findOneById [coll=Knowledge]
    knowledge [
        
      @findOneById <>
      subject
      
    ]

  >
  
  video <
  
    @findOne <query={_id:@}>
    subject
        
  >
  
  knowledge [
    
    @findOne <query={_id:@}>
    subject
    
    @findOne [query={_id:@}]
    child [
      
      @findOne <query={_id:@}>
      subject
      
    ]
    
  ]

>
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
* query

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
* \[ \]  屬性值底下的每個 item 為 @

若屬性值本身不是集合，則 \[ \] 和 { } 的 @ 指向同一個物件

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
