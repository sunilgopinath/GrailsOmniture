class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/reportSuite/list"(controller:"reportSuite"){
                    action = [GET: "list"]
		}
		"/reportSuite/mostpopular/$reportsuiteid?/$date?/$count?"(controller:"reportSuite", parseRequest: true){
                    action = [GET: "mostpopular", POST: "mostpopular"]
		}
		"/reportSuite/report/$reportsuiteid"(controller:"reportSuite", parseRequest: true){
                    action = [GET: "getReport"]
		}
		"/reportSuite/index"(controller:"reportSuite", parseRequest: true){
                    action = [GET: "index", POST:"index"]
		}
		"/"(view:"/index")
		"500"(view:'/error')
	}
}
