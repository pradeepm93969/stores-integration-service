function init() {
	changeTitle();
}

function handleDownloadCommonConfigurations() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/commonConfigurations/extract?" + $("form[name=searchCommonConfigurationsForm]").serialize(),
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
		    link.download="CommonConfigurations_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchCommonConfigurations() {
	
	var searchData = "pageSize=" + $("#commonConfigurationsTableSection #pageSize").val() + "&pageNo=" + $("#commonConfigurationsTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/commonConfigurations?" + searchData + $("form[name=searchCommonConfigurationsForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#commonConfigurationsTableSection').hasClass('hidden')) {
				$('#commonConfigurationsTableSection').toggleClass('hidden');
			}
			$('#commonConfigurationsTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#commonConfigurationsTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","value"];
				$('#commonConfigurationsTableSection table tbody').html(constructTableRows(true, true, true, false, 'CommonConfigurations', data, paramsArray));
				constructPagination('commonConfigurationsTableSection', $("#commonConfigurationsTableSection #pageSize").val(), $("#commonConfigurationsTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteCommonConfigurations(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/commonConfigurations/" + id,
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
					handleSearchCommonConfigurations();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateCommonConfigurations(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/commonConfigurations/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-commonconfigurations-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateCommonConfigurationsForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateCommonConfigurations() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateCommonConfigurationsForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/commonConfigurations/" + formData.id,
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
				handleSearchCommonConfigurations();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createCommonConfigurations() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-commonconfigurations-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateCommonConfigurations() {
	$('#loader').show(0);
	
	$.ajax({
		type : "POST",
		url : "/api/commonConfigurations/",
		data: JSON.stringify(getFormData($("#mainModal form[name=createCommonConfigurationsForm]"))),
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
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

