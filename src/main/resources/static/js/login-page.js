/* loing.html 에서 field-error 발생 후 input 입력시 error 클래스 삭제 */
document.addEventListener("DOMContentLoaded", function () {
    const errorInputs = document.querySelectorAll("input.field-error-input");

    errorInputs.forEach(function (input) {
        input.addEventListener("input", function () {
            if (this.value.trim().length > 0) {
                this.classList.remove("field-error-input");
            }
        });
    });
});