import { stringToFileBuffer } from '@angular-devkit/core/src/virtual-fs/host';
import { Component, OnInit } from '@angular/core';
import * as Highcharts from 'highcharts/highcharts';
import HighchartsMore from 'highcharts/highcharts-more';
import { ClusterService } from 'src/app/service/cluster.service';

HighchartsMore(Highcharts);
@Component({
  selector: 'app-search-page',
  templateUrl: './search-page.component.html',
  styleUrls: ['./search-page.component.scss'],
})
export class SearchPageComponent implements OnInit {
  query: string = '';
  bubblechart: any;
  constructor(private clusterService?: ClusterService) {}
  Highcharts = Highcharts;
  clusterData:any;
  reindexData:any;

  ngOnInit(): void {
    this.page_render();
  }

  public page_render() {
    this.clusterService?.reindex_data()
    console.log('page render');
    const el = document.getElementById('input_query') as HTMLInputElement;

    el?.addEventListener('keypress', (event) => {
      if (event.code === 'Enter') {
        this.cluster_search(JSON.stringify(el.value));
      }
    });
  }

  public cluster_search(query: any) {
    console.log('cluster search call query' + query);
    this.fetch_cluster_data(query);
    
  }

  public fetch_cluster_data(query:any) {
    //service call cluster service and fetch json object
    console.log('fetch cluster data call');
    this.clusterService?.cluster_data(query);
    this.clusterService?.myBehaviorSubject.subscribe(
      (data) => {
        console.log('JSON DATA',data);
        if(data){
          // this.clean_cluster_data(data)
          this.clusterData = data;
          this.highchart_function(query);
        }
      }
    );
    
  }

  public clean_cluster_data(cluster_response:any){
    //console.log(cluster_response.length)
  }
  
  public reindex_cluster_data(){
    const value = this.clusterService?.reindex_data();
    
    this.clusterService?.myreindexBehaviourSubject.subscribe((rdata)=>{
      this.reindexData = rdata;

        if(this.reindexData.success){
          console.log("reindex call",this.reindexData.success)
        }
    })
     
  }

  
  public highchart_function(query: any) {

      this.bubblechart=Highcharts.chart('container',{
        plotOptions: {
          series: {
            
            allowPointSelect:true,
                cursor: 'pointer',
                point: {
                  events: {
                      select: function (event) {
                          var text = this.name + ': ' + this.y + ' was last selected'
                          console.log(text);
                          const cls = new SearchPageComponent();
                          cls.clickchart(event,this.name)
                          
                      }
                  }
              }
              },
          packedbubble: {
            minSize: '20%',
            maxSize: '100%',
            layoutAlgorithm: {
              gravitationalConstant: 0.05,
              splitSeries: true,
              seriesInteraction: false,
              dragBetweenSeries: false,
              parentNodeLimit: true
            },
            dataLabels: {
              enabled: true,
              format: '{point.name}',
              filter: {
                property: 'y',
                operator: '>',
                value: 1
              },
              style: {
                color: 'black',
                textOutline: 'none',
                fontWeight: 'normal'
              }
            }
          }
        },

        tooltip: {
          useHTML: true,
          pointFormat: '<b>{point.name}:</b> {point.value}',
          
      },

      series: 
      this.clusterData,
      chart: {
        type: 'packedbubble',
      },
      title: {
        text: 'Query Search',
      },

    }
    )
      
  }

  public clickchart(event: any, query: any) {
    // alert(' clicked\n' + event);
    window.open('https://en.wikipedia.org/wiki/' + query);
  }
}
