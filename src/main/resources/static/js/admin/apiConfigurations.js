function init() {
	changeTitle();
}

function handleDownloadApiConfigurations() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/apiConfigurations/extract?" + $("form[name=searchApiConfigurationsForm]").serialize(),
		beforeSend : function(xhr) {
			/* Authorization header */
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			var blob=new Blob([data]);
		    var link=document.createElement('a');
		    link.href=window.URL.createObjectURL(blob);
		    link.download="ApiConfigurations_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchApiConfigurations() {
	
	var searchData = "pageSize=" + $("#apiConfigurationsTableSection #pageSize").val() + "&pageNo=" + $("#apiConfigurationsTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/apiConfigurations?" + searchData + $("form[name=searchApiConfigurationsForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#apiConfigurationsTableSection').hasClass('hidden')) {
				$('#apiConfigurationsTableSection').toggleClass('hidden');
			}
			$('#apiConfigurationsTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#apiConfigurationsTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","url","method","authenticationType","username","password","customValue","timeoutInSeconds","customHeader","systemName"];
				$('#apiConfigurationsTableSection table tbody').html(constructTableRows(true, true, true, false, 'ApiConfigurations', data, paramsArray));
				constructPagination('apiConfigurationsTableSection', $("#apiConfigurationsTableSection #pageSize").val(), $("#apiConfigurationsTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteApiConfigurations(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/apiConfigurations/" + id,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			beforeSend : function(xhr) {
				xhr.setRequestHeader("Authorization", "Bearer "
						+ localStorage.getItem('token'));
				xhr.setRequestHeader("X-Mobile", "false");
			},
			success : function(data) {
				$('#loader1').hide(0);
				displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully deleted...', function() {
					$('#alertPopup').toggleClass('hidden');
					handleSearchApiConfigurations();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateApiConfigurations(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/apiConfigurations/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-apiconfigurations-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateApiConfigurationsForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateApiConfigurations() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateApiConfigurationsForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/apiConfigurations/" + formData.id,
		data: JSON.stringify(formData),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully updated...', function() {
				$('#alertPopup').toggleClass('hidden');
				handleSearchApiConfigurations();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createApiConfigurations() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-apiconfigurations-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateApiConfigurations() {
	$('#loader').show(0);
	$.ajax({
		type : "POST",
		url : "/api/apiConfigurations",
		data: JSON.stringify(getFormData($("#mainModal form[name=createApiconfigurationsForm]"))),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			displayAlertPopup('fa-check-circle', 'text-green-600', 'Successfully created...', function() {
				$('#alertPopup').toggleClass('hidden');
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			handleApplicationError(data);
		}
	});
}

