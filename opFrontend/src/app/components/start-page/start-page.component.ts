import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { DateChapter } from 'src/app/model/dataChapter';

@Component({
  selector: 'app-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.scss']
})
export class StartPageComponent implements OnInit {

  DataChapter: any;

  constructor(private http:HttpClient) { }

  ngOnInit() {
   let response= this.http.get("http://localhost:8282/getDateAndChapter");
   response.subscribe((data)=> this.DataChapter=data);
  }

}
