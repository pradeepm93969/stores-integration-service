function init() {
	changeTitle();
}

function handleDownloadSmsConfigurations() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/smsConfigurations/extract?" + $("form[name=searchSmsConfigurationsForm]").serialize(),
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
		    link.download="SmsConfigurations_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchSmsConfigurations() {
	
	var searchData = "pageSize=" + $("#smsConfigurationsTableSection #pageSize").val() + "&pageNo=" + $("#smsConfigurationsTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/smsConfigurations?" + searchData + $("form[name=searchSmsConfigurationsForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#smsConfigurationsTableSection').hasClass('hidden')) {
				$('#smsConfigurationsTableSection').toggleClass('hidden');
			}
			$('#smsConfigurationsTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#smsConfigurationsTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","enabled","whitelisted","whitelistedNumbers","message"];
				$('#smsConfigurationsTableSection table tbody').html(constructTableRows(true, true, true, false, 'SmsConfigurations', data, paramsArray));
				constructPagination('smsConfigurationsTableSection', $("#smsConfigurationsTableSection #pageSize").val(), $("#smsConfigurationsTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteSmsConfigurations(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/smsConfigurations/" + id,
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
					handleSearchSmsConfigurations();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateSmsConfigurations(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/smsConfigurations/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-smsconfigurations-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateSmsConfigurationsForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateSmsConfigurations() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateSmsConfigurationsForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/smsConfigurations/" + formData.id,
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
				handleSearchSmsConfigurations();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createSmsConfigurations() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-smsconfigurations-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateSmsConfigurations() {
	$('#loader').show(0);
	
	$.ajax({
		type : "POST",
		url : "/api/smsConfigurations/",
		data: JSON.stringify(getFormData($("#mainModal form[name=createSmsConfigurationsForm]"))),
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

