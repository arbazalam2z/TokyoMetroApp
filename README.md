Tokyo Metro Application Contest.
-------------------------------------------------------------------------------
東京メトロアプリコンテストのサンプルアプリ.

アプリを実行するには次の対応が必要. 

下記2ファイルにそれぞれ開発者用キーを登録する. 

`/res/values/google_maps_api.xml`
GoogleMap用開発者キー

`/res/values/metro_sonsumer_key.xml`
東京メトロWebAPI用コンシュマーキー

TODO
-------------------------------------------------------------------------------

 1. sameAsのequals判定最適化. reverse matching.
 2. ContentRepositoryの初期化フロー見直し.
 3. GoogleMapパフォーマンスの最適化
 4. 永続化データの有効活用
 5. 駅詳細データのページをユーザライクに

sameAsのequals判定最適化. reverse matching.
StationやRailwayのsameAsをデータオブジェクトのIDとして使用している. 
IDはHashMapのkeyとしても使用されており, sameAsの同一性比較は頻繁に行われる. 
sameAsはjavaのパッケージ名のようにより広域なドメインを先頭にドット"."区切りで定義されている. 
つまりは前半部分の文字列はほとんどのsameAs文字列で揃っており, ここの同一性比較はほぼ無駄になる. 
携帯電話番号の090,080と同じように, sameAsを比較する際はより差異の発見を早めるよう文字列を反転
させてから比較する方が効率がいい. 

ContentRepositoryの初期化フロー見直し.
東京メトロから駅データを取得する必要があるが, これは適切なタイミングで実施しないと, 
トラフィックやリソースの無駄になる. 
現状アプリの起動時に毎回データ更新チェックを実施しているためここを見直す必要がある. 
(AsyncServiceを導入するなど)

GoogleMap上に駅マーカーを配置するにあたり, 同一座標に駅が複数存在することを考慮して, 
Google Map Utilityのclusteringを導入している. 
ただし, Clusteringアニメーションのためのパフォーマンスに難がある. 
Google Mapの無料版で確認: https://developers.google.com/maps/licensing?hl=ja

現状, 初回初期化以降の高速化を目的にActiveAndroidによるモデルデータの永続化を行って
いるが, 各所でデータベースを使用するか, フェッチするか, メモリ上のデータを取得するかを
より細かく制御するコントローラを配置する見直しが必要である. 

現状, Stationモデルの情報をただ並べて表示しているだけであるため, 
これを見やすくするUIにする必要がある. (時刻表等)

Copyright
------------------------------------------------------------------------------
> Copyright 2014 yuki312 All Right Reserved.
> 
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
> 
>    http://www.apache.org/licenses/LICENSE-2.0
> 
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

Use Library
------------------------------------------------------------------------------

 - [Volley](https://android.googlesource.com/platform/frameworks/volley/)
 - [Google Map](https://developers.google.com/maps/?hl=ja)
 - [Google Maps Android API Utility Library](https://developers.google.com/maps/documentation/android/utility/)
 - [ActiveAndroid](http://www.activeandroid.com/)
 - [EventBus](https://github.com/greenrobot/EventBus)
 - [guava](https://code.google.com/p/guava-libraries/)
 - [gson](https://code.google.com/p/google-gson/)
 - [android-power-assert](https://github.com/gfx/android-power-assert-plugin)