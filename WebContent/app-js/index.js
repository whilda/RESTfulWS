$("#overlay, .closeButton").on("click",function(){hideOver();});
$(".signupbtn").on("click",function(){signup();showOver();});
$(".loginbtn").on("click",function(){login();showOver();});
function showOver(){
	$("#overlay").fadeIn("slow");
	$("#overlayBox").fadeIn("slow");
}
function hideOver(){
	$("#overlay").fadeOut("slow");
	$("#overlayBox").fadeOut("slow");
}
function login(){
	$("#login").show();
	$("#register").hide();
}
function signup(){
	$("#login").hide();
	$("#register").show();
}