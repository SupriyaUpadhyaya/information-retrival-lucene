import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ClusterService {

  url="http://localhost:8080/search";
  reindex_url="http://localhost:8080/indexing";

  // url="../../assets/cluster_data.json";
  // reindex_url="../../assets/reindex.json";

  myBehaviorSubject = new BehaviorSubject("");
  myreindexBehaviourSubject = new BehaviorSubject("")
  data:any;
  reindexdata:any;
  constructor(private http:HttpClient) { }

  public cluster_data(query:any){
    let queryParams = new HttpParams();
    // queryParams = queryParams.append("x",5);
    queryParams= queryParams.append("q",query.replace(/^"(.+(?="$))"$/, '$1'));
    
    this.http.get(this.url,{params:queryParams}).subscribe((query_data)=>{
    this.data= query_data;
    this.myBehaviorSubject.next(this.data)
    });
  }

  public reindex_data(){
    this.http.get(this.reindex_url).subscribe((data)=>{
      this.reindexdata = data;
      alert("reindexing  Done")
      // console.log(this.reindexdata.success)
      this.myreindexBehaviourSubject.next(this.reindexdata)
    })
  }
}
