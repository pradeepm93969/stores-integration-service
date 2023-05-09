function init() {
	changeTitle();
}

function handleSearchIntegrationLog() {
	var searchData = "pageSize=" + $("#integrationLogTableSection #pageSize").val() + "&pageNo=" + $("#integrationLogTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	
	$.ajax({
		type : "GET",
		url : "/api/integrationLog?" + searchData + serializeFormData("searchIntegrationLogForm"),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#integrationLogTableSection').hasClass('hidden')) {
				$('#integrationLogTableSection').toggleClass('hidden');
			}
			$('#integrationLogTableSection tbody').html('');
			if (data.content.length > 0) {
				$('#integrationLogTableSection #selectAll').prop('checked',false);
				var rows = "";
				for (var i = 0; i < data.content.length; i++) {
					rows += "<tr>";
					rows += "<td class='td-action'><button type='button' class='bg-primary-400 hover:bg-primary-300 text-white text-md font-bold py-1 px-2 border-b-4" + 
					"border-primary-500 hover:border-primary-500 rounded-lg mr-1' onclick='getRequest(\"" + data.content[i].id + "\")'><i class='fas fa-eye'></i></button></td>";
					rows += "<td class='td-action'><button type='button' class='bg-primary-400 hover:bg-primary-300 text-white text-md font-bold py-1 px-2 border-b-4" + 
					"border-primary-500 hover:border-primary-500 rounded-lg mr-1' onclick='getResponse(\"" + data.content[i].id + "\")'><i class='fas fa-eye'></i></button></td>";
					rows += "<td>" + data.content[i].id + "</td>";
					rows += "<td>" + (data.content[i].createdAt === undefined ? "" :  data.content[i].createdAt) + "</td>";
					rows += "<td>" + (data.content[i].identifier === undefined ? "" : data.content[i].identifier) + "</td>";
					rows += "<td>" + (data.content[i].subIdentifier === undefined ? "" :  data.content[i].subIdentifier) + "</td>";
					rows += "<td>" + (data.content[i].operationType === undefined ? "" :  data.content[i].operationType) + "</td>";
					rows += "<td>" + (data.content[i].systemName === undefined ? "" :  data.content[i].systemName) + "</td>";
					rows += "<td>" + (data.content[i].url === undefined ? "" :  data.content[i].url) + "</td>";
					rows += "<td>" + (data.content[i].method === undefined ? "" :  data.content[i].method) + "</td>";
					rows += "<td>" + data.content[i].responseTime + "</td>";
					rows += "<td>" + data.content[i].responseStatus + "</td>";
					rows += "</tr>";
				}
				$('#integrationLogTableSection tbody').html(rows);
				
				constructPagination('integrationLogTableSection', $("#integrationLogTableSection #pageSize").val(), $("#integrationLogTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function getRequest(id) {
	$('#loader').show(0);
	
	$.ajax({
		type : "GET",
		url : "/api/integrationLog/" + id + "/request",
		dataType : "text",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.request-modal-container').html());
			$('#mainModal .card-body').html(data);
			$('#mainPopup').toggleClass('hidden');
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function getResponse(id) {
	$('#loader').show(0);
	
	$.ajax({
		type : "GET",
		url : "/api/integrationLog/" + id + "/response",
		dataType : "text",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer " + localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.response-modal-container').html());
			$('#mainModal .card-body').html(data);
			$('#mainPopup').toggleClass('hidden');
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

