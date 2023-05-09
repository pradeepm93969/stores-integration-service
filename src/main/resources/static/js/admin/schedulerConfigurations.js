function init() {
	changeTitle();
}

function handleDownloadSchedulerConfigurations() {
	
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/schedulerConfigurations/extract?" + $("form[name=searchSchedulerConfigurationsForm]").serialize(),
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
		    link.download="SchedulerConfigurations_" + new Date().toLocaleString().replace(',','').replaceAll('/','-').replace(' ','T').replace(' ','') + ".csv";
		    link.click();
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}

function handleSearchSchedulerConfigurations() {
	
	var searchData = "pageSize=" + $("#schedulerConfigurationsTableSection #pageSize").val() + "&pageNo=" + $("#schedulerConfigurationsTableSection #pageNo").text() + "&";
	$('#loader1').show(0);
	$.ajax({
		type : "GET",
		url : "/api/schedulerConfigurations?" + searchData + $("form[name=searchSchedulerConfigurationsForm]").serialize(),
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader1').hide(0);
			if ($('#schedulerConfigurationsTableSection').hasClass('hidden')) {
				$('#schedulerConfigurationsTableSection').toggleClass('hidden');
			}
			$('#schedulerConfigurationsTableSection table tbody').html('');
			if (data.content.length > 0) {
				$('#schedulerConfigurationsTableSection #selectAll').prop('checked',false);
				var paramsArray = ["id","enabled","status","threadCount","retryCount","batchSize","restartSeconds","exponentialFactor","nextRunSeconds","debug","lastSuccessfulStartAt","lastSuccessfulEndAt"];
				$('#schedulerConfigurationsTableSection table tbody').html(constructTableRows(true, true, true, false, 'SchedulerConfigurations', data, paramsArray));
				constructPagination('schedulerConfigurationsTableSection', $("#schedulerConfigurationsTableSection #pageSize").val(), $("#schedulerConfigurationsTableSection #pageNo").text(), data);
			}
		},
		error : function(data) {
			$('#loader1').hide(0);
			handleApplicationError(data);
		}
	});
}


function deleteSchedulerConfigurations(id) {
	displayDeletePopup(function() {
		$('#deletePopup').toggleClass('hidden');
		$('#loader1').show(0);
		$.ajax({
			type : "DELETE",
			url : "/api/schedulerConfigurations/" + id,
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
					handleSearchSchedulerConfigurations();
				});
			},
			error : function(data) {
				$('#loader1').hide(0);
				handleApplicationError(data);
			}
		});
	});
}

function updateSchedulerConfigurations(id) {
	$('#loader').show(0);
	$.ajax({
		type : "GET",
		url : "/api/schedulerConfigurations/" + id,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		beforeSend : function(xhr) {
			xhr.setRequestHeader("Authorization", "Bearer "
					+ localStorage.getItem('token'));
			xhr.setRequestHeader("X-Mobile", "false");
		},
		success : function(data) {
			$('#loader').hide(0);
			$('#mainModal').html($('.update-schedulerconfigurations-modal-container').html());
			$('#mainPopup').toggleClass('hidden');
			loadFormData($('#mainModal form[name=updateSchedulerConfigurationsForm]'), data);
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function handleUpdateSchedulerConfigurations() {
	$('#loader').show(0);
	
	var formData = getFormData($("#mainModal form[name=updateSchedulerConfigurationsForm]"));
	
	$.ajax({
		type : "PUT",
		url : "/api/schedulerConfigurations/" + formData.id,
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
				handleSearchSchedulerConfigurations();
			});
		},
		error : function(data) {
			$('#loader').hide(0);
			$('#mainPopup').toggleClass('hidden');
			handleApplicationError(data);
		}
	});
}

function createSchedulerConfigurations() {
	$('#loader').show(0);
	$('#mainModal').html($('.create-schedulerconfigurations-modal-container').html());
	$('#mainPopup').toggleClass('hidden');
	$('#loader').hide(0);
}

function handleCreateSchedulerConfigurations() {
	$('#loader').show(0);
	
	$.ajax({
		type : "POST",
		url : "/api/schedulerConfigurations/",
		data: JSON.stringify(getFormData($("#mainModal form[name=createSchedulerConfigurationsForm]"))),
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

