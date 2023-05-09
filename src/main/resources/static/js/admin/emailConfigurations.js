function init() {
	changeTitle();
}

function handleDownloadEmailConfigurations() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/emailConfigurations/extract?" + $("form[name=searchEmailConfigurationsForm]").serialize(),
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
		    link.download="EmailConfigurations_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchEmailConfigurations() {
	
	var searchData = "pageSize=" + $("#emailConfigurationsTableSection #pageSize").val() + "&pageNo=" + $("#emailConfigurationsTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/emailConfigurations?" + searchData + $("form[name=searchEmailConfigurationsForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#emailConfigurationsTableSection').hasClass('hidden')) {
				$('#emailConfigurationsTableSection').toggleClass('hidden');
			}
			$('#emailConfigurationsTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#emailConfigurationsTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","enabled","subject","allowedDomain"];
				$('#emailConfigurationsTableSection table tbody').html(constructTableRows(true, true, true, false, 'EmailConfigurations', data, paramsArray));
				constructPagination('emailConfigurationsTableSection', $("#emailConfigurationsTableSection #pageSize").val(), $("#emailConfigurationsTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteEmailConfigurations(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/emailConfigurations/" + id,
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
					handleSearchEmailConfigurations();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateEmailConfigurations(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/emailConfigurations/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-emailconfigurations-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateEmailConfigurationsForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateEmailConfigurations() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateEmailConfigurationsForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/emailConfigurations/" + formData.id,
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
				handleSearchEmailConfigurations();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createEmailConfigurations() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-emailconfigurations-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateEmailConfigurations() {
	$('#loader').show(0);
	
	$.ajax({
		type : "POST",
		url : "/api/emailConfigurations/",
		data: JSON.stringify(getFormData($("#mainModal form[name=createEmailConfigurationsForm]"))),
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

