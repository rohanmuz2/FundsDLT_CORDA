"use strict";

// Similar to the IOU creation modal - see createIOUModal.js for comments.
angular.module('demoAppModule').controller('TransferModalCtrl', function ($http, $uibModalInstance, $uibModal, apiBaseURL, peers, id) {
    const transferModal = this;

    transferModal.peers = peers;
    transferModal.id = id;
    transferModal.form = {};
    transferModal.formError = false;

    transferModal.transfer = () => {
        if (invalidFormInput()) {
            transferModal.formError = true;
        } else {
            transferModal.formError = false;

            const id = transferModal.id;
            const kycstatus = transferModal.form.kycstatus;

            $uibModalInstance.close();

            const issueIOUEndpoint =
                apiBaseURL +
                `kyc?id=${id}&kycstatus=${kycstatus}&`;

            $http.get(issueIOUEndpoint).then(
                (result) => transferModal.displayMessage(result),
                (result) => transferModal.displayMessage(result)
            );
        }
    };

    transferModal.displayMessage = (message) => {
        const transferMsgModal = $uibModal.open({
            templateUrl: 'transferMsgModal.html',
            controller: 'transferMsgModalCtrl',
            controllerAs: 'transferMsgModal',
            resolve: { message: () => message }
        });

        transferMsgModal.result.then(() => {}, () => {});
    };

    transferModal.cancel = () => $uibModalInstance.dismiss();

    function invalidFormInput() {
        return false;
    }
});

angular.module('demoAppModule').controller('transferMsgModalCtrl', function ($uibModalInstance, message) {
    const transferMsgModal = this;
    transferMsgModal.message = message.data;
});