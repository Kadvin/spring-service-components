/**
 * Created by Sin on 2014/8/18.
 */
angular.module('Index.Dialog',['ui.bootstrap','dialogs.main','dialogs.default-translations','pascalprecht.translate'])
    .factory('DialogService',function($rootScope, $timeout, dialogs){

        //-- Variables --//

        var _progress = 33;

        //-- Methods --//

        var launch = function(which, title, context){
            switch(which){
                case 'error':
                    dialogs.error(title,context);
                    break;
                case 'wait':
                    dialogs.wait(undefined,undefined,_progress);
                    _fakeWaitProgress();
                    break;
                case 'customwait':
                    dialogs.wait('Custom Wait Header','Custom Wait Message',_progress);
                    _fakeWaitProgress();
                    break;
                case 'notify':
                    dialogs.notify(title,context);
                    break;
                case 'confirm':
                    var dlg = dialogs.confirm();
                    dlg.result.then(function(btn){
                        $rootScope.confirmed = 'You confirmed "Yes."';
                    },function(btn){
                        $rootScope.confirmed = 'You confirmed "No."';
                    });
                    break;
            }
        }; // end launch

        var _fakeWaitProgress = function(){
            $timeout(function(){
                if(_progress < 100){
                    _progress += 33;
                    $rootScope.$broadcast('dialogs.wait.progress',{'progress' : _progress});
                    _fakeWaitProgress();
                }else{
                    $rootScope.$broadcast('dialogs.wait.complete');
                }
            },1000);
        };

        return launch;
    }) // end controller(dialogsServiceTest)

    .config(['dialogsProvider', '$translateProvider', function(dialogsProvider, $translateProvider){
        dialogsProvider.useBackdrop('static');
        dialogsProvider.useEscClose(false);
        dialogsProvider.useCopy(false);
        dialogsProvider.setSize('sm');

        $translateProvider.preferredLanguage('en-US');
    }])
;
