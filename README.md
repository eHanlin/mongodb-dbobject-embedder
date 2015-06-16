# mongodb-dbobject-embedder
>使用簡易的語法進行 mongodb 的資料查詢及再處理

## 簡易範例

#### Example 1
>執行 db.user.find({ height : { $gte : 215 } }) 的原始資料

```javascript
[
  { 
    "_id" : ObjectId("557e58727a8ea2a9dfe2ef76"), 
    "name" : "Kirk", 
    "postal_code" : ObjectId("557e56287a8ea2a9dfe2ef71"), 
    "height" : 220, 
    "friends" : [ 
      ObjectId("557e58727a8ea2a9dfe2ef74"), 
      ObjectId("557e58727a8ea2a9dfe2ef77"), 
      ObjectId("557e58727a8ea2a9dfe2ef7a"), 
      ObjectId("557e58727a8ea2a9dfe2ef7e")] 
  },
  { 
    "_id" : ObjectId("557e58727a8ea2a9dfe2ef7a"), 
    "name" : "Rick", 
    "postal_code" : ObjectId("557e56287a8ea2a9dfe2ef72"), 
    "height" : 218, 
    "friends" : [ 
      ObjectId("557e58727a8ea2a9dfe2ef76"), 
      ObjectId("557e58727a8ea2a9dfe2ef7c"), 
      ObjectId("557e58727a8ea2a9dfe2ef7e")] 
  }
]
```

>下面的 DSL 將會把 user 這個 collection 中身高大於 215 的使用者撈出，
>加入一個新欄位 num，值是同一個 postal_code 中身高大於 200 的使用者數，
>並且將其原本以 ObjectId 記錄的 postal_code 資料嵌入，
>再將 friends 中的 postal_code 嵌入。

```
@find <db=user coll=user query={ height : { $gte : 215 } }>
[
  @count (db=user coll=user query={ height : { $gte : 200 } , postal_code : @.postal_code })
  num

  @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
  postal_code

  @findOneById [db=user coll=user, projection={ _id : 0 , postal_code : 1, height : 1}]
  friends
  [
    @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
    postal_code
  ]
]
```

>執行 DSL 之後的結果

```javascript
[
  {
    "_id":{"$oid":"557e58727a8ea2a9dfe2ef76"},
    "name":"Kirk",
    "postal_code":{"name":"中正區"},
    "height":220,
    "friends":[
      {"postal_code":{"name":"中正區"},"height":201},
      {"postal_code":{"name":"中正區"},"height":211},
      {"postal_code":{"name":"信義區"},"height":218},
      {"postal_code":{"name":"內湖區"},"height":208}],
    "num":3
  },
  {
    "_id":{"$oid":"557e58727a8ea2a9dfe2ef7a"},
    "name":"Rick",
    "postal_code":{"name":"信義區"},
    "height":218,
    "friends":[
      {"postal_code":{"name":"中正區"},"height":220},
      {"postal_code":{"name":"信義區"},"height":214},
      {"postal_code":{"name":"內湖區"},"height":208}],
    "num":4
  }
]
```

## Repository
> Maven

```
<repositories>
  <repository>
    <id>mongodb-dbobject-embedder</id>
    <url>http://dl.bintray.com/hotdog929/maven</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>tw.com.ehanlin</groupId>
    <artifactId>mongodb-dbobject-embedder</artifactId>
    <version>0.0.1</version>
  </dependency>
</dependencies>
```

> Sbt

```
resolvers += "mongodb-dbobject-embedder" at "http://dl.bintray.com/hotdog929/maven"
libraryDependencies ++= Seq(
  "tw.com.ehanlin" % "mongodb-dbobject-embedder" % "0.0.1"
)
```

> Gradle

```
repositories {
    maven {
        url "http://dl.bintray.com/hotdog929/maven"
    }
}
dependencies {
    compile 'tw.com.ehanlin:mongodb-dbobject-embedder:0.0.1'
}
```

> Grape

```
@GrabResolver(name='mongodb-dbobject-embedder', root='http://dl.bintray.com/hotdog929/maven')
@Grab('tw.com.ehanlin:mongodb-dbobject-embedder:0.0.1')
```

## 使用方式

```java

Mongo mongo = new Mongo(host, port)

//設定 default db
MongoEmbedder.registerDB(mongo.getDB("info"))

//設定各 db
MongoEmbedder.registerDB("info", mongo.getDB("info"))
MongoEmbedder.registerDB("user", mongo.getDB("user"))

//執行 DSL
MongoEmbedder.instance.embed(null, "@find <db=user coll=user query={ height : { $gte : 215 } }> [ ]")

```


## 語法說明

### 屬性的迭代作用域
>可以用在屬性上的作用城有二種
* < content... > 會將傳入的值當一個單一值處理
* [ content... ] 會將傳入的值當集合處理，依傳入值的類型，會有以下三種情況：
  * List ： for(v in List){List[index] = embed(v)} return List
  * Map ： for(k,v in Map){Map[k] = embed(v)} return Map
  * Other ： return [ embed(Other) ] as List
* 巢狀迭代是合法的，比如 [[ content... ]] 對應到巢狀 List 時的執行方式是 for(v in List){List[index] = for(vv in v){embed(vv)}} return List

#### Example 2  < content... >
>執行 db.user.findOne({ _id : ObjectId("557e58727a8ea2a9dfe2ef7a") }) 的原始資料

```javascript
{ 
  "_id" : ObjectId("557e58727a8ea2a9dfe2ef7a"), 
  "name" : "Rick", 
  "postal_code" : ObjectId("557e56287a8ea2a9dfe2ef72"), 
  "height" : 218, 
  "friends" : [ 
    ObjectId("557e58727a8ea2a9dfe2ef76"), 
    ObjectId("557e58727a8ea2a9dfe2ef7c"), 
    ObjectId("557e58727a8ea2a9dfe2ef7e")] 
}
```

>DSL

```
@findOne <db=user coll=user query={ _id : { $oid : "557e58727a8ea2a9dfe2ef7a" } }>
<
  @findOneById <db=info coll=postal_code projection={ _id : 0 }>
  postal_code
  <
    @findOne <db=info coll=city query={ _id : { $oid : @ } } projection={ _id : 0 }>
    city
    <
      @findOneById <db=info coll=country projection={ _id : 0 }>
      country
    >
  >
>
```

>執行 DSL 之後的結果

```javascript
{
  "_id":{"$oid":"557e58727a8ea2a9dfe2ef7a"},
  "name":"Rick",
  "postal_code":{
    "country":"TW",
    "city":{
      "country":{
        "name":"臺灣"},
      "name":"臺北市"},
    "code":"110",
    "name":"信義區"},
  "height":218,
  "friends":[
    {"$oid":"557e58727a8ea2a9dfe2ef76"},
    {"$oid":"557e58727a8ea2a9dfe2ef7c"},
    {"$oid":"557e58727a8ea2a9dfe2ef7e"}]
}
```

#### Example 3  [ content... ] Map
>執行 db.team.find({ _id : "zebra" }) 的原始資料

```javascript
{
  "_id" : "zebra",
  "name" : "zebra",
  "player" : {
    "c" : ObjectId("557e58727a8ea2a9dfe2ef76"),
    "f" : ObjectId("557e58727a8ea2a9dfe2ef77"),
    "g" : ObjectId("557e58727a8ea2a9dfe2ef7b")}
}
```

>DSL

```
@findOne <db=user coll=team query={ _id : "zebra" } projection={ _id : 0 }>
<
  @findOneById [db=user coll=user projection={ _id : 0 , name : 1 , postal_code : 1 }]
  player
  [
    @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
    postal_code
  ]
>
```

>執行 DSL 之後的結果

```
{
  "name":"zebra",
  "player":{
    "c":{
      "name":"Kirk",
      "postal_code":{"name":"中正區"}},
    "f":{
      "name":"Mick",
      "postal_code":{"name":"中正區"}},
    "g":{
      "name":"Sean",
      "postal_code":{"name":"信義區"}}}
}
```

#### Example 4  [[ content... ]] List[ List... ]
>執行 db.team.find({ _id : ObjectId("557e58727a8ea2a9dfe2ef83") }) 的原始資料

```javascript
{
  "_id" : ObjectId("557e58727a8ea2a9dfe2ef83"),
  "team" : ["zebra","snake"],
  "box_score" : [
    [
      ObjectId("557e58727a8ea2a9dfe2ef86"),
      ObjectId("557e58727a8ea2a9dfe2ef87"),
      ObjectId("557e58727a8ea2a9dfe2ef88")
    ],
    [
      ObjectId("557e58727a8ea2a9dfe2ef89"),
      ObjectId("557e58727a8ea2a9dfe2ef8a"),
      ObjectId("557e58727a8ea2a9dfe2ef8b")
    ]
  ]
}
```

>DSL

```
@findOne <db=game coll=game query={ _id : { $oid : "557e58727a8ea2a9dfe2ef83" } } projection={ _id : 0 }>
<
  box_score
  [
    @findOneById [db=game coll=box_score projection={ _id : 0 , user : 1 , pts : 1 }]
    [
      @findOneById <db=user coll=user projection={ _id : 0 , name : 1 }>
      user
    ]
  ]
>
```

>執行 DSL 之後的結果

```
{
  "team":["zebra","snake"],
  "box_score":[
    [
      {"user":{"name":"Kirk"},"pts":11},
      {"user":{"name":"Mick"},"pts":5},
      {"user":{"name":"Sean"},"pts":5}
    ],
    [
      {"user":{"name":"Toby"},"pts":3},
      {"user":{"name":"Eric"},"pts":6},
      {"user":{"name":"Glen"},"pts":6}
    ]
  ]
}
```


### Action 的迭代作用域
>可以用在屬性上的作用城有三種
* @Action ( info... ) 位在 info 中的 @ 會以屬性的父層代入
* @Action < info... > 位在 info 中的 @ 會以屬性本身代入
* @Action [ info... ] 若屬性本身是個集合，則位在 info 中的 @ 會以各子項帶入，不然會以屬性本身代入但回傳 List

#### Example 5  ( info... )
>執行 db.postal_code.find({},{name:1}) 的原始資料

```
[
  { "_id" : ObjectId("557e56287a8ea2a9dfe2ef71"), "name" : "中正區" },
  { "_id" : ObjectId("557e56287a8ea2a9dfe2ef72"), "name" : "信義區" },
  { "_id" : ObjectId("557e56287a8ea2a9dfe2ef73"), "name" : "內湖區" }
]
```

>DSL

```
@find <db=info coll=postal_code projection={ name : 1 }>
[
  @count (db=user coll=user query={ height : { $gte : 200 } , postal_code : @._id })
  num
]
```

>執行 DSL 之後的結果

```
[
  {
    "_id":{"$oid":"557e56287a8ea2a9dfe2ef71"},
    "name":"中正區",
    "num":3
  },
  {
    "_id":{"$oid":"557e56287a8ea2a9dfe2ef72"},
    "name":"信義區",
    "num":4
  },
  {
    "_id":{"$oid":"557e56287a8ea2a9dfe2ef73"},
    "name":"內湖區",
    "num":1
  }
]
```

#### Example 6  < info... >
>執行 db.postal_code.find({},{_id:0,code:0}) 的原始資料

```
[
  { "country" : "TW", "city" : "557e55af7a8ea2a9dfe2ef70", "name" : "中正區" },
  { "country" : "TW", "city" : "557e55af7a8ea2a9dfe2ef70", "name" : "信義區" },
  { "country" : "TW", "city" : "557e55af7a8ea2a9dfe2ef70", "name" : "內湖區" }
]
```

>DSL

```
@find <db=info coll=postal_code projection={ _id : 0 , code : 0 }>
[
  @findOne (db=info coll=country query={ _id : @.country } projection={ _id : 0 })
  country

  @findOne (db=info coll=city query={ _id : { $oid : @.city } } projection={ _id : 0 , country : 0 })
  city
]
```

>執行 DSL 之後的結果

```
[
  {
    "country":{"name":"臺灣"},
    "city":{"name":"臺北市"},
    "name":"中正區"
  },
  {
    "country":{"name":"臺灣"},
    "city":{"name":"臺北市"},
    "name":"信義區"
  },
  {
    "country":{"name":"臺灣"},
    "city":{"name":"臺北市"},
    "name":"內湖區"
  }
]
```

#### Example 7  [ info... ]


### 目前支援的 Action
* @find
  * db
  * coll
  * projection
  * query

* @findOne
  * db
  * coll
  * projection
  * query

* @findOneById
  * db
  * coll
  * projection

* @distinct
  * db
  * coll
  * key
  * query

* @count
  * db
  * coll
  * query

* @aggregate
  * db
  * coll
  * pipelines

#### Example 8  all action

### 預計實作的 Action
* @dsl
  * db
  * coll
  * dsl


### 使用自訂 Action

#### Example 9  Custom Action
