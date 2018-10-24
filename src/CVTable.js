import React from 'react';
import Cvrow from './Cvrow.js'

class CVTable extends React.Component {

      state = { userName: '', userId: '', prevId: '' }
    handleReset = (event) => {
        var empty = [];
        this.props.onClick(empty);
    };
    

    render() {
        return (
            <div className="tableRight" style={{ visibility: this.props.visibility }}>
                {/* <button onClick={this.handleReset}>Back</button> */}
                <table className="table" id="cvTable">
                    <thead>
                        <tr className="row header" onClick={this.props.handleVisibility}>
                            {/* <th className="cell">CV ID</th> */}
                             <th className="cell">File Name</th>
                            <th className="cell">Status</th>
                            <th className="cell">Download</th>
                            <th className="cell">Delete</th>
                        </tr></thead>
                    {this.props.cvs.map(cv => <Cvrow onClick={this.props.onClick}   cvs={this.props.cvs} prevId={this.state.prevId} viewerId = {this.props.viewerId} key={cv.cvId} findCv={this.props.findCv} {...cv} />)}
                </table>
            </div>
        );
    }
}

export default CVTable;


