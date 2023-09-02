/* 자바스크립트로 버튼(삭제/수정) API 구현*/

// [ 삭제 기능 ]
// : HTML에서 id가 'delete-btn'로 설정된 요소를 찾아, 그 요소에서 클릭이벤트가 발생하면 fetch()로 삭제 API 요청 보냄.
const deleteButton = document.getElementById('delete-btn');
if (deleteButton) {
    deleteButton.addEventListener('click', event => {
        let id = document.getElementById('article-id').value;
        fetch(`/api/articles/${id}`, {
            method: 'DELETE'
        })
            .then(() => {
                alert('삭제가 완료되었습니다.');
                location.replace('/articles');
            });
    });
}

// [ 수정 기능 ]
// id가 'modify-btn' 인 요소(Element) 조회
const modifyButton= document.getElementById('modify-btn');

// 클릭 이벤트가 감지되면 수정 API 요청.
if (modifyButton) {
    modifyButton.addEventListener('click', event => {
        let params = new URLSearchParams(location.search);
        let id = params.get('id');

        fetch(`/api/articles/${id}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById('title').value,
                content: document.getElementById('content').value
            })
        })
            .then(() => {
                alert('수정이 완료되었습니다.');
                location.replace(`/articles/${id}`);
            });
    });
}


// [ 생성 기능 ]
// id가 'create-btn' 인 요소(Element) 조회
const createButton= document.getElementById('create-btn');

// 클릭 이벤트가 감지되면 생성 API 요청.
if(createButton) {
    createButton.addEventListener("click", (event) => {
        fetch("/api/articles", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                title: document.getElementById("title").value,
                content: document.getElementById("content").value,
            }),
        }).then(() => {
            alert("등록 완료하였습니다.");
            location.replace("/articles");
        });
    });
}
